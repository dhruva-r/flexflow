from transformers import pipeline
classifier = pipeline("text-classification",model='bhadresh-savani/distilbert-base-uncased-emotion', return_all_scores=True)
prediction = classifier("""Today was a difficult day, filled with a heavy sense of sadness and sorrow. It started with waking up feeling overwhelmed and anxious, 
        unable to shake off the melancholy that lingered from the night before. The weather seemed to mirror my emotions, with gray clouds overhead 
        and a steady drizzle outside. It felt as though the world was reflecting my sadness. I received some unfortunate news in the morning, 
        which left me feeling heartbroken and helpless. It's challenging to find the strength to cope with such adversity, and I found myself 
        seeking solace in solitude. Throughout the day, memories of happier times haunted me, intensifying the feeling of loss and longing. 
        The weight of grief seemed unbearable, making even the simplest tasks feel like daunting challenges. Interactions with others were strained, 
        as I struggled to find the right words to express how I was feeling. It felt as though I was carrying a heavy burden that I couldn't share.
        As the day went on, I tried to distract myself with various activities, but the sadness was ever-present, casting a shadow over everything I did.
        In the evening, I allowed myself to grieve and shed tears for the pain that seemed to consume me. Sometimes, acknowledging our sadness is necessary 
        to begin the process of healing. Despite the sorrow, I know that tomorrow is a new day, and I hope that with time, the weight on my heart will ease, 
        and I'll find the strength to move forward.""", )
print(prediction[0])

# sadness, anger, fear, joy, surprise, love
activities_dict = {
    "grounding_meditation.mp3": ["sadness", "surprise", "fear"],
    "yoga": ["sadness", "anger", "fear"],
    "jog": ["joy", "surprise", "anger"],
    "meditation_for_relationships.mp3": ["joy", "love", "sadness"],
    "awareness_of_self_meditation.mp3": ["love, anger, sadness"] #https://www.youtube.com/watch?v=Aho7Tsya8BI&ab_channel=DianeLinsley-GuidedMeditation
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

    print("Suggested activity: " + max_activity)

suggest_activities(prediction)