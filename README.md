# DataminingWatson

## Contents of this file

 - Introduction
 - Requirements
 - Compiling & Running the code
 - Using the functions

## Introduction
The main purpose of this project is to build a part of IBM’s Watson.
Watson is a project that aims to create a simplified search engine capable of retrieving relevant Wikipedia articles based on user queries. The project utilizes a subset of questions from Jeopardy games, with answers corresponding to Wikipedia pages. The system performs data preprocessing, indexing, and search operations using Lucene.



## Requirements
 - IntelliJ IDEA


## Compiling & Running the code
All needed to be done for set-up is just the cloning of the GitHub project. After that, Compiling and running the code is done normally.
When you first run the code, use the first option from the menu to create the indexes.

**Note**: In case IntelliJ does not recognize questions.txt changing the path to an absolute one may be advised.



## Using the functions
`create index `
-	It deletes the existing indexes from the wiki-index folder
-	Creates a document for every article with a stored index on title and category
  
`run question`
- The user inputs a category and a clue
- The system converts them into a query
- The query is used to search for the most relevant article
- The output is a list of the most relevant articles with its’ size equal to the max number of retrieved documents
- If the question is from questions.txt, the output will also contain if the result is a hit or a perfect hit

`run question with AI`
- The user inputs a category and a clue
- The system converts them into a query
-	The query is used to search for the most relevant article 
-	The output is a list of the most relevant articles with its’ size equal to the max number of retrieved documents and the same list reranked by the AI
-	If the question is from questions.txt, the output will also contain if the result is a hit or a perfect hit
 
`run default questions and measure`
-	The system searches the most relevant article using the categories and clues from questions.txt 
-	The output is a list of the most relevant articles with its’ size equal to the max number of retrieved documents and if the result is a hit or a perfect hit
- The output also contains the NDCG and MRR metrics

`set max number of retrieved documents (> 0 - default 10)`
-	The user inputs the desired max number of retrieved documents  




