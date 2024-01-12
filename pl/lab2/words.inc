%include 'colon.inc'

section .data
global dict_ptr

colon "key3", third_key
db "value 3", 0

colon "key2", second_key
db "value 2", 0

colon "key1", dict_ptr
db "value 1", 0
