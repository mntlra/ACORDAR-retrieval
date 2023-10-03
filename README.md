# An Analysis of the ACORDAR Test Collection: Retrieval Results

This repository contains the code we used to reproduce the retrieval results present in the [ACORDAR Test Collection's reference paper](https://doi.org/10.1145/3477495.3531729). The program indexes the given corpus and retrieve the 10 most relevant datasets for the given queries. 

## Repository Structure
- `experiments/` contains all the results produced by the code: the indexes and propduced runs.
- `src/main/java/acordar/retrieval` contains the source code we developed to index and search among datasets.
- `resources/`: contains useful resources to run the code, including the NLTK stoplist and the ACORDAR Test Collection.
- `target/`: contains the executable jar file.

## Before Starting

### Acquire the Resources
The program indexes and searches among datasets starting from the corpus of ACORDAR provided by the authors in their GitHub repository. To run our code, first clone the ACORDAR repository inside the resources directory.

#### Clone the ACORDAR Test Collection

Go to the resources directory
```bash
$  cd resources
```

Clone the ACORDAR Test Collection
```bash
$  git clone https://github.com/nju-websoft/ACORDAR/
```

#### Acquire datasets' content

To run the Content and Full configuration you need to have access to the datasets' content. **Note**: you can run the Metadata configuration without acquiring the datasets' content just set the `contentPath` argument to any valid directory.

You can clone the [ACORDAR Retrieve Datasets Content GitHub repository](https://github.com/mntlra/ACORDAR-Repro-py) and follow the instruction to run the code. 

Clone the ACORDAR Retrieve Datasets Content GitHub repository
```bash
$  git clone https://github.com/mntlra/ACORDAR-Repro-py
```
and follow the instructions to download and parse the datasets.

**Alternative**: you can download the indexable content from zenodo (_**url available upon acceptance**_) and place it in the directory `resources/indexable_content/`.

### Prepare the environment
We provide a `pom.xml` file with all the required dependecies and an executable jar file. 

## Usage

Users can run the executable jar file `target/acordar-retrieval-1.0-jar-with-dependencies.jar `. Users can also deploy the code using the `AcordarRetrieval.java` file and setting the following arguments (remember to set the parameters in the correct order):

0.  `corpusPath`: specifies the path to the corpus file to index.

1. `similarity`: specifies the similarity function to employ during indexing and during search. Accepted values are: `TFIDF` for TF-IDF (ClassicSimilarity), `BM25` for BM25F (BM25Similarity), `FSDM` for FSDM re-ranker, and `LMD`for Language Model using Dirichlet priors for smoothing (LMDirichletSimilarity).

2. `mode`: specifies the run configuration. Accepted values are: `Metadata` for indexing only metadata, `Content`for indexing only the data fields, and `Full` for indexing metadata and content.

3. `contentPath`: specifies the path to the indexable content. If content has been downloaded to zenodo in the correct repository, set it to `resources/indexable_content`. If content has been acquired with the [ACORDAR Content Retrieve GitHub Repository](https://github.com/mntlra/ACORDAR-Repro-py), set it to the path to the `output/indexbale_content` directory. If you run the `Metadata` configuration this argument is not used within the code so you can set it to any valid directory.

4.  `topicsPath`: specifies the path to the queries.

5.  `runDirectory`: specifies the path to the directory containing all runs. The produced runs will be saved in the directory: `runDirectory/mode/`.

6. `boostWeights`: specified whether to use the boost weights or not. To use the boost weights set the parameter to `boost`, otherwise set it to anything else.

### Example: Run the JAR file

To run the Metadata configuration using BM25F and boost weights over the complete test collection and all queries (we assume the datasets' content is in the directory `resources/indexable_content/`):

```bash
$ java -jar target/acordar-retrieval-1.0-jar-with-dependencies.jar 
resources/ACORDAR/Data/datasets.json BM25 Metadata resources/indexable_content/ resources/ACORDAR/Data/all_queries.txt 
experiments/runs/ boost
```
