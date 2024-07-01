from datasets import load_dataset
from transformers import logging, AutoTokenizer, DataCollatorWithPadding, pipeline, create_optimizer, TFAutoModelForSequenceClassification
import evaluate
import numpy as np
import tensorflow as tf
from transformers.keras_callbacks import KerasMetricCallback, PushToHubCallback
import sklearn
import torch

logging.set_verbosity(4)
accuracy = evaluate.load("accuracy")
tokenizer = AutoTokenizer.from_pretrained("distilbert-base-uncased")
imdb = load_dataset("imdb")
print(imdb)

#tokenize and truncate sequences to be no longer than DistilBERT's maximum input length
def preprocess_function(examples):
    return tokenizer(examples["text"], truncation=True)

#apply preprocessing to entire dataset
tokenized_imdb = imdb.map(preprocess_function, batched=True)

#create batch of examples. dynamically pad the sentences to the longest length in a batch during collation
data_collator = DataCollatorWithPadding(tokenizer=tokenizer)

def compute_metrics(eval_pred):
    predictions, labels = eval_pred
    predictions = np.argmax(predictions, axis=1)
    return accuracy.compute(predictions=predictions, references=labels) 

id2label = {    
    0: "NEUTRAL",
    1: "HAPPY",
    2: "SAD",
    3: "ANGRY",
    4: "SURPRISED",
    5: "EXCITED",
    6: "CALM",
    7: "ANXIOUS",
    8: "HOPEFUL",
    9: "FEARFUL",
    10: "CONFIDENT",
    11: "CONFUSED",
    12: "GRATEFUL",
    13: "PROUD",
    14: "RELIEVED",
    15: "ENTHUSIASTIC",
    16: "LONELY",
    17: "BORED",
    18: "SATISFIED",
    19: "CURIOUS",
    20: "SYMPATHETIC",
    21: "CONTENT",
    22: "INSPIRED",
    23: "HOPELESS",
    24: "DISAPPOINTED",
    25: "EMBARRASSED",
    26: "AMUSED",
    27: "HUMBLE",
    28: "DETERMINED",
    29: "LOVING"
}

label2id = {    
    "NEUTRAL": 0,
    "HAPPY": 1,
    "SAD": 2,
    "ANGRY": 3,
    "SURPRISED": 4,
    "EXCITED": 5,
    "CALM": 6,
    "ANXIOUS": 7,
    "HOPEFUL": 8,
    "FEARFUL": 9,
    "CONFIDENT": 10,
    "CONFUSED": 11,
    "GRATEFUL": 12,
    "PROUD": 13,
    "RELIEVED": 14,
    "ENTHUSIASTIC": 15,
    "LONELY": 16,
    "BORED": 17,
    "SATISFIED": 18,
    "CURIOUS": 19,
    "SYMPATHETIC": 20,
    "CONTENT": 21,
    "INSPIRED": 22,
    "HOPELESS": 23,
    "DISAPPOINTED": 24,
    "EMBARRASSED": 25,
    "AMUSED": 26,
    "HUMBLE": 27,
    "DETERMINED": 28,
    "LOVING": 29
}

batch_size = 16
num_epochs = 5
batches_per_epoch = len(tokenized_imdb["train"]) // batch_size
total_train_steps = int(batches_per_epoch * num_epochs)
optimizer, schedule = create_optimizer(init_lr=2e-5, num_warmup_steps=0, num_train_steps=total_train_steps)

#instantiate the model with params
model = TFAutoModelForSequenceClassification.from_pretrained(
    "distilbert-base-uncased", num_labels=30, id2label=id2label, label2id=label2id
)

# prepare training and validation sets
tf_train_set = model.prepare_tf_dataset(
    tokenized_imdb["train"],
    shuffle=True,
    batch_size=16,
    collate_fn=data_collator,
)

tf_validation_set = model.prepare_tf_dataset(
    tokenized_imdb["test"],
    shuffle=False,
    batch_size=16,
    collate_fn=data_collator,
)

model.compile(optimizer=optimizer)

# compute accuracy from predictions
metric_callback = KerasMetricCallback(metric_fn=compute_metrics, eval_dataset=tf_validation_set)

callbacks = [metric_callback]
model.fit(x=tf_train_set, validation_data=tf_validation_set, epochs=3, callbacks=callbacks)

text = "This was a masterpiece. Not completely faithful to the books, but enthralling from beginning to end. Might be my favorite of the three."
classifier = pipeline("sentiment-analysis", model="d352wang/fydp_model_1")
classifier(text)