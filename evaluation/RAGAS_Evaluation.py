from ragas import evaluate
from ragas.metrics import faithfulness, answer_relevancy, context_precision, context_recall
from ragas.llms import LangchainLLMWrapper
from langchain_anthropic import ChatAnthropic
from ragas.embeddings import LangchainEmbeddingsWrapper
from langchain_voyageai import VoyageAIEmbeddings
from dotenv import load_dotenv
import requests
import json
import os
from datasets import Dataset
import pandas as pd
import time

load_dotenv()

embeddings = LangchainEmbeddingsWrapper(VoyageAIEmbeddings(
    model="voyage-4-lite",
    voyage_api_key=os.getenv("VOYAGEAI_API_KEY")
))

answer_relevancy.embeddings = embeddings

llm = LangchainLLMWrapper(ChatAnthropic(
    model="claude-sonnet-4-6",
    anthropic_api_key= os.getenv("ANTHROPIC_API_KEY")
))

faithfulness.llm = llm
answer_relevancy.llm = llm
context_precision.llm = llm
context_recall.llm = llm

current_dir = os.path.dirname(__file__)
file_path = os.path.join(current_dir, "golden_dataset.json")
url = "http://localhost:8080/api/document/query"

questions = []
answers = []
contexts = []
ground_truths = []

with open(file_path, "r") as f:
    golden_dataset = json.load(f)

for entry in golden_dataset:
    question = entry["question"]
    response = requests.post(url, json={"question":question})
    answer = response.text

    questions.append(question)
    answers.append(answer)
    contexts.append(entry["reference_contexts"])
    ground_truths.append(entry["ground_truth"])
    
    
data = Dataset.from_dict({
    "question": questions,
    "answer": answers,
    "contexts": contexts,
    "ground_truth": ground_truths,
})

results = evaluate(data, metrics=[faithfulness, answer_relevancy, context_precision, context_recall])

df = results.to_pandas()
print(df)
df.to_csv("ragas_results.csv", index=False)