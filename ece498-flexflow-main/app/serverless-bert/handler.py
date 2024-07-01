import json
from transformers import pipeline

# try:
#     import unzip_requirements
# except ImportError:
#     pass
# from model.model import ServerlessModel
# import json

# model = ServerlessModel('./model', "flexflowbucket", "")
def classify(event, context):       
    classifier = pipeline("text-classification",model='bhadresh-savani/bert-base-uncased-emotion', top_k=None)
    print(event["body"])
    prediction = classifier(event["body"],)
    #prediction = model.predict(event["body"])
    print(prediction[0])

    # sadness, anger, fear, joy, surprise, love
    activities_dict = {
        "grounding_meditation.mp3": ["sadness", "surprise", "fear"],
        "yoga": ["sadness", "anger", "fear"],
        "jog": ["joy", "surprise", "anger"],
        "meditation_for_relationships.mp3": ["joy", "love", "sadness"],
        "awareness_of_self_meditation.mp3": ["love", "anger", "sadness"], #https://www.youtube.com/watch?v=Aho7Tsya8BI&ab_channel=DianeLinsley-GuidedMeditation
    }

    # Find activity with highest sum of emotion scores
    def suggest_activities(prediction):
        moods = {item['label']: item['score'] for item in prediction[0]}
        print(moods)
        max_activity = ""
        max_score = 0
        for task in activities_dict:
            curr_score = 0
            for emotion in activities_dict[task]:
                curr_score += moods[emotion]
                if curr_score > max_score:
                    max_score = curr_score
                    max_activity = task
        print(max_activity)
        return max_activity
    
    return {
        'statusCode': 200,
        'body': json.dumps(suggest_activities(prediction))
    }
