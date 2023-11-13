//===================this file is not associated with the project in 'src'
//applying threads
import threading
class CustomThread(Thread):
    # constructor
    def __init__(self):
        # execute the base constructor
        Thread.__init__(self)
        # set a default value
        self.value = None
def sort_even_numbers(arr):
    even_numbers = [num for num in arr if num % 2 == 0]
    even_numbers.sort()
    return even_numbers

def sort_odd_numbers(arr):
    odd_numbers = [num for num in arr if num % 2 != 0]
    odd_numbers.sort()
    return odd_numbers

def merge_results(arr, even_numbers, odd_numbers):
    for i in range(len(even_numbers)):
        arr[i] = even_numbers[i]
    for i in range(len(odd_numbers)):
        arr[i + len(even_numbers)] = odd_numbers[i]
    return arr

arr = [2, 29, 3, 0, 11, 8, 32, 94, 9, 1, 7]

thread1 = threading.Thread(target=sort_even_numbers, args=(arr,))
thread2 = threading.Thread(target=sort_odd_numbers, args=(arr,))

thread1.start()
thread2.start()

thread1.join()
even_numbers = thread1.result

thread2.join()
odd_numbers = thread2.result

merged_result = merge_results(arr, even_numbers, odd_numbers)
print(merged_result)
