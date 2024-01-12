import subprocess
import unittest

class Test(unittest.TestCase):
    def test_first_key(self):
        input_data = b'key1'
        process = subprocess.Popen('./main', stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate(input=input_data)
        expected_output = b'value 1'
        self.assertEqual(stdout, expected_output)
        self.assertEqual(stderr, b'')

    def test_second_key(self):
        input_data = b'key2'
        process = subprocess.Popen('./main', stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate(input=input_data)
        expected_output = b'value 2'
        self.assertEqual(stdout, expected_output)
        self.assertEqual(stderr, b'')

    def test_third_key(self):
        input_data = b'key3'
        process = subprocess.Popen('./main', stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate(input=input_data)
        expected_output = b'value 3'
        self.assertEqual(stdout, expected_output)
        self.assertEqual(stderr, b'')

    def test_unknown_key(self):
        input_data = b'Some_random_words_for_testing'
        process = subprocess.Popen('./main', stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate(input=input_data)
        self.assertEqual(stdout, b'')
        self.assertEqual(stderr, b'No such key')

    def test_key_too_long(self):
        input_data = b'Some_more_random_words_for_testing' + b'_and_more' * 100
        process = subprocess.Popen('./main', stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate(input=input_data)
        self.assertEqual(stdout, b'')
        self.assertEqual(stderr, b'Key is too long')

if __name__ == '__main__':
    unittest.main()