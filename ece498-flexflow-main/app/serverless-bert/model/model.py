from transformers import AutoModelForSequenceClassification, AutoTokenizer, AutoConfig
import torch
import boto3
import os
import tarfile
import io
import base64
import json
import re

s3 = boto3.client('s3')

class ServerlessModel:
    def __init__(self, model_path=None, s3_bucket=None, file_prefix=None):
        self.model, self.tokenizer = self.from_pretrained(
            model_path, s3_bucket, file_prefix)

    def from_pretrained(self, model_path: str, s3_bucket: str, file_prefix: str):
        model = self.load_model_from_s3(model_path, s3_bucket, file_prefix)
        tokenizer = self.load_tokenizer(model_path)
        return model, tokenizer

    def load_model_from_s3(self, model_path: str, s3_bucket: str, file_prefix: str):
        if model_path and s3_bucket and file_prefix:
            obj = s3.get_object(Bucket=s3_bucket, Key=file_prefix)
            bytestream = io.BytesIO(obj['Body'].read())
            tar = tarfile.open(fileobj=bytestream, mode="r:gz")
            config = AutoConfig.from_pretrained(f'{model_path}/config.json')
            for member in tar.getmembers():
                if member.name.endswith(".bin"):
                    f = tar.extractfile(member)
                    state = torch.load(io.BytesIO(f.read()))
                    model = AutoModelForSequenceClassification.from_pretrained(
                        pretrained_model_name_or_path=None, state_dict=state, config=config)
            return model
        else:
            raise KeyError('No S3 Bucket and Key Prefix provided')

    def load_tokenizer(self, model_path: str):
        tokenizer = AutoTokenizer.from_pretrained(model_path)
        return tokenizer

    def encode(self, text):
        # Adjusted to handle single text input for emotion detection
        encoded = self.tokenizer.encode_plus(text, return_tensors='pt', max_length=512, truncation=True, padding='max_length')
        return encoded["input_ids"], encoded["attention_mask"]

    def predict(self, text):
        input_ids, attention_mask = self.encode(text)
        outputs = self.model(input_ids, attention_mask=attention_mask)
        logits = outputs.logits
        probabilities = torch.softmax(logits, dim=1)
        # Optionally, return the top emotion and its probability
        top_prob, top_label_idx = torch.max(probabilities, dim=1)
        return top_prob.item(), self.tokenizer.convert_ids_to_tokens(int(top_label_idx))
