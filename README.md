# SkipList in Java – Data Structures & Algorithms

This project provides two implementations of the **Skip List** data structure in Java. It is designed to help users understand how Skip Lists work and how different values of the `alpha` probability parameter affect performance.

Skip Lists are an alternative to balanced trees for efficient searching, insertion, and deletion. This project allows users to **experiment with different alpha values** (`0.0`, `0.25`, and `0.5`) to observe their impact on efficiency.

It includes:
- A **Basic Version** without generics, designed for simplicity, which only accepts `Integer` values.
- A **Templates Version** using `Node<T>`, allowing flexibility in handling different data types.
- Example **input/output files** for functional testing:
  - `input_example_1.txt` and `input_example_2.txt` – contain basic operations.
  - `output_example_1.txt` and `output_example_2.txt` – show the expected output for the above inputs.
- **Performance test datasets** using different values of the `alpha` probability parameter:
  - `alphaEfficiencyTest_10K_1.txt` – 10,000 elements, `alpha = 0.0`
  - `alphaEfficiencyTest_10K_2.txt` – 10,000 elements, `alpha = 0.25`
  - `alphaEfficiencyTest_10K_3.txt` – 10,000 elements, `alpha = 0.5`
  - `alphaEfficiencyTest_100K_1.txt` – 100,000 elements, `alpha = 0.0`
  - `alphaEfficiencyTest_100K_2.txt` – 100,000 elements, `alpha = 0.25`
  - `alphaEfficiencyTest_100K_3.txt` – 100,000 elements, `alpha = 0.5`



## 📚 What is a Skip List?

A **Skip List** is a probabilistic data structure that enhances a **sorted linked list** with multiple levels to speed up search, insertion, and deletion operations. It achieves an average **O(log n)** time complexity, similar to balanced trees, but with simpler implementation and **automatic self-balancing**.

### 🔹 How does a Skip List work?
- The **bottom level** is a fully ordered linked list, ensuring the correct order (like a standard array/list).
- Each **top level** contains a subset of the elements of the level below it, which act as shortcuts for faster navigation.
- The probability that an element will create these top levels is controlled by the `alpha` parameter (usually `0.5`).
- Searches start at the top level, and search by row if the value is larger then I advance to the next element on the same row, if it is smaller then I go back one and go down a level.
- If I am at the first level and I don't find the element then it doesn't exist.

### 🔹 Is Skip List usefull? Of course!
- **Fast operations**: Average **O(log n)** time complexity for search, insertion, and deletion.
- **Simplicity**: Easier to implement than AVL or Red-Black Trees.
- **Automatic balancing**: No need for explicit rebalancing, unlike trees that require rotations.
- **Concurrency-friendly**: Well-suited for multi-threaded applications due to its independent levels.
- **Efficient in large-scale applications**: Skip Lists are widely used in **databases, caching systems, and distributed computing** for their efficiency and ordered data retrieval capabilities.

## 📂 Project Structure

```
SkipList-Java/
├── SkipList_Base/
│   └── SkipList_Base.java
├── SkipList_Template/
│   └── SkipList_Template.java
├── IO_FILES/
│   ├── input_example_1.txt
│   ├── output_example_1.txt
├── alphaEfficiencyTest/
│   ├── alphaEfficiencyTest_100K_1.txt
│   └── ...
```

## How to Compile and Run

1. Open a terminal in the `SkipList_Base` or `SkipList_Template` folder.
2. Compile the program:
   ```bash
   javac SkipList_Base.java  # or SkipList_Template.java
   ```
3. Run the program with an input file:
   ```bash
   java SkipList_Base ../IO_FILES/input_example_1.txt
   ```
   o
   ```bash
   java SkipList_Base ../IO_FILES/input_example_1.txt
   ```

## 🧩 Supported Operations

The input file contains commands for:
- `0` → Print the minimum element.
- `1` → Remove the minimum element.
- `2 key value` → Insert an element.
- `3` → Print the Skip List.

## 📊 Performance Testing

The `alphaEfficiencyTest/` folder contains datasets used to analyze Skip List efficiency based on the `alpha` parameter. The tests evaluate:
- Execution time for different dataset sizes.
- The average number of traversed nodes per operation.
- How different alpha values (`0.0`, `0.25`, and `0.5`) impact performance.


## @Author

Developed by **Jacopo** for the study of data structures and algorithms in Italy, Univeristy of Padua.
