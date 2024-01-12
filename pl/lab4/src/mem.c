#define _DEFAULT_SOURCE

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "mem.h"
#include "mem_internals.h"
#include "util.h"

static bool try_merge_with_next(struct block_header *block);

void debug_block(struct block_header *b, const char *fmt, ...);
void debug(const char *fmt, ...);

extern inline block_size size_from_capacity(block_capacity cap);
extern inline block_capacity capacity_from_size(block_size sz);

static bool block_is_big_enough(size_t query, struct block_header *block) {
  return block->capacity.bytes >= query;
}
static size_t pages_count(size_t mem) {
  return mem / getpagesize() + ((mem % getpagesize()) > 0);
}
static size_t round_pages(size_t mem) {
  return getpagesize() * pages_count(mem);
}

static void block_init(void *restrict addr, block_size block_sz,
                       void *restrict next) {
  *((struct block_header *)addr) = (struct block_header){
      .next = next, .capacity = capacity_from_size(block_sz), .is_free = true};
}

static size_t region_actual_size(size_t query) {
  return size_max(round_pages(query), REGION_MIN_SIZE);
}

extern inline bool region_is_invalid(const struct region *r);

static void *map_pages(void const *addr, size_t length, int additional_flags) {
  return mmap((void *)addr, length, PROT_READ | PROT_WRITE,
              MAP_PRIVATE | MAP_ANONYMOUS | additional_flags, -1, 0);
}

static struct region alloc_region(void const *addr, size_t query) {
  size_t offset = offsetof(struct block_header, contents);
  size_t size = region_actual_size(query + offset);
  void *mapped_pages = map_pages(addr, size, MAP_FIXED_NOREPLACE);

  if (mapped_pages == MAP_FAILED) {
    mapped_pages = map_pages(addr, size, 0);
    if (mapped_pages == MAP_FAILED) {
      return REGION_INVALID;
    }
  }
  block_init(mapped_pages, (block_size){size}, NULL);

  return (struct region){
      .addr = mapped_pages, .size = size, .extends = addr == mapped_pages};
}

static void *block_after(struct block_header const *block);

void *heap_init(size_t initial) {
  struct region region = alloc_region(HEAP_START, initial);
  if (region_is_invalid(&region)) {
    return NULL;
  }
  return region.addr;
}

void heap_term() {
  struct block_header *curr = HEAP_START;
  while (curr) {
    _free(curr->contents);
    curr = curr->next;
  }
  curr = HEAP_START;
  while (curr) {
    while (try_merge_with_next(curr))
      ; // Это выглядит максимально по уродски
    struct block_header *next = curr->next;
    munmap(curr, round_pages(size_from_capacity(curr->capacity).bytes));
    curr = next;
  }
}

#define BLOCK_MIN_CAPACITY 24

static bool block_splittable(struct block_header *restrict block,
                             size_t query) {
  return block->is_free &&
         query + offsetof(struct block_header, contents) + BLOCK_MIN_CAPACITY <=
             block->capacity.bytes;
}

static bool split_if_too_big(struct block_header *block, size_t query) {
  if (query < BLOCK_MIN_CAPACITY) {
    query = BLOCK_MIN_CAPACITY;
  }
  if (!block_splittable(block, query)) {
    return false;
  }
  block_size first_size = size_from_capacity((block_capacity){query});
  block_size new_size = {size_from_capacity(block->capacity).bytes -
                         first_size.bytes};

  void *new_block = (uint8_t *)block + first_size.bytes;
  block_init(new_block, new_size, block->next);
  block_init(block, first_size, new_block);
  return true;
}

static void *block_after(struct block_header const *block) {
  return (void *)(block->contents + block->capacity.bytes);
}

static bool blocks_continuous(struct block_header const *fst,
                              struct block_header const *snd) {
  return (void *)snd == block_after(fst);
}

static bool mergeable(struct block_header const *restrict fst,
                      struct block_header const *restrict snd) {
  return fst->is_free && snd->is_free && blocks_continuous(fst, snd);
}

static bool try_merge_with_next(struct block_header *block) {
  if (!block->next) {
    return false;
  }
  if (!mergeable(block, block->next)) {
    return false;
  }
  block->capacity.bytes += size_from_capacity(block->next->capacity).bytes;
  struct block_header *next = block->next;
  if (next) {
    next = next->next;
  }
  block->next = next;
  return true;
}

enum block_search_result_type {
  BSR_FOUND_GOOD_BLOCK,
  BSR_REACHED_END_NOT_FOUND
};

struct block_search_result {
  enum block_search_result_type type;
  struct block_header *block;
};

struct block_search_result
funkziya_constructor_po_zaprosu_bogdana(enum block_search_result_type type,
                                        struct block_header *block) {
  return (struct block_search_result){.type = type, .block = block};
}

static struct block_search_result
find_good_or_last(struct block_header *restrict block, size_t sz) {
  struct block_header *curr = block;
  while (curr->next) {
    while (try_merge_with_next(curr))
      ; // Лдьрййъъ
    if (curr->is_free && block_is_big_enough(sz, curr)) {
      return funkziya_constructor_po_zaprosu_bogdana(BSR_FOUND_GOOD_BLOCK,
                                                     curr);
    }
    curr = curr->next;
  }

  bool is_good = curr->is_free && block_is_big_enough(sz, curr);
  int type = is_good ? BSR_FOUND_GOOD_BLOCK : BSR_REACHED_END_NOT_FOUND;

  return funkziya_constructor_po_zaprosu_bogdana(type, curr);
}

static struct block_search_result
try_memalloc_existing(size_t query, struct block_header *block) {
  struct block_search_result result = find_good_or_last(block, query);
  if (result.type != BSR_FOUND_GOOD_BLOCK) {
    return result;
  }

  struct block_header *good_block = result.block;
  split_if_too_big(good_block, query);
  good_block->is_free = false;
  return funkziya_constructor_po_zaprosu_bogdana(BSR_FOUND_GOOD_BLOCK,
                                                 good_block);
}

static struct block_header *grow_heap(struct block_header *restrict last,
                                      size_t query) {
  if (!last) {
    return NULL;
  }

  void *end_of_last = block_after(last);
  struct region new_reg = alloc_region(end_of_last, query);
  if (region_is_invalid(&new_reg)) {
    return NULL;
  }

  last->next = new_reg.addr;
  try_merge_with_next(last);

  if (!last->next) {
    return last;
  }

  return last->next;
}

static struct block_header *memalloc(size_t query,
                                     struct block_header *heap_start) {
  struct block_search_result result = try_memalloc_existing(query, heap_start);
  if (result.type == BSR_FOUND_GOOD_BLOCK) {
    return result.block;
  }

  struct block_header *last_block = heap_start;
  while (last_block->next) {
    last_block = last_block->next;
  }

  struct block_header *new_block = grow_heap(last_block, query);
  if (!new_block) {
    return NULL;
  }

  result = try_memalloc_existing(query, new_block);
  if (result.type == BSR_FOUND_GOOD_BLOCK) {
    return result.block;
  }

  return NULL;
}

void *_malloc(size_t query) {
  struct block_header *const addr =
      memalloc(query, (struct block_header *)HEAP_START);
  if (addr) {
    return addr->contents;
  }
  return NULL;
}

static struct block_header *block_get_header(void *contents) {
  return (struct block_header *)(((uint8_t *)contents) -
                                 offsetof(struct block_header, contents));
}

void _free(void *mem) {
  if (!mem) {
    return;
  }
  struct block_header *header = block_get_header(mem);
  header->is_free = true;
  while (try_merge_with_next(header))
    ;
}
