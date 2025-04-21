class MinHeap:
    """Custom implementation of a min heap data structure."""
    
    def __init__(self):
        self.heap = []
    
    def push(self, item):
        """Add item to the heap and maintain the heap property."""
        self.heap.append(item)
        self._sift_up(len(self.heap) - 1)
    
    def pop(self):
        """Remove and return the smallest item from the heap."""
        if not self.heap:
            return None
        
        # Swap the root with the last element
        self._swap(0, len(self.heap) - 1)
        item = self.heap.pop()
        
        # Restore the heap property
        if self.heap:
            self._sift_down(0)
        return item
    
    def _sift_up(self, index):
        """Move an item up the heap to maintain the heap property."""
        parent = (index - 1) // 2
        
        if index > 0 and self.heap[parent][0] > self.heap[index][0]:
            self._swap(index, parent)
            self._sift_up(parent)
    
    def _sift_down(self, index):
        """Move an item down the heap to maintain the heap property."""
        smallest = index
        left = 2 * index + 1
        right = 2 * index + 2
        
        if left < len(self.heap) and self.heap[left][0] < self.heap[smallest][0]:
            smallest = left
            
        if right < len(self.heap) and self.heap[right][0] < self.heap[smallest][0]:
            smallest = right
            
        if smallest != index:
            self._swap(index, smallest)
            self._sift_down(smallest)
    
    def _swap(self, i, j):
        """Swap two items in the heap."""
        self.heap[i], self.heap[j] = self.heap[j], self.heap[i]
    
    def is_empty(self):
        """Check if the heap is empty."""
        return len(self.heap) == 0