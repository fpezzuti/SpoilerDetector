import re
import unidecode
import time
import string
import nltk 
from nltk.corpus import stopwords # Stopwords 
from nltk.tokenize import word_tokenize # Word_tokenizer
import contractions
#from pattern.text.en import singularize

# Remove html parts from text
def remove_links(text):
    text = re.sub(r'http\S+', '', text)
    return text

# Code for accented characters removal
def accented_characters_removal(text):
    """
    The function will remove accented characters from the 
    text contained within the Dataset.
       
    arguments:
        input_text: "text" of type "String". 
                    
    return:
        value: "text" with removed accented characters.
        
    Example:
    Input : Málaga, àéêöhello
    Output : Malaga, aeeohello    
        
    """
    # Remove accented characters from text using unidecode.
    # Unidecode() - It takes unicode data & tries to represent it to ASCII characters. 
    text = unidecode.unidecode(text)
    return text

     # Code for removing repeated characters and punctuations

def reducing_incorrect_character_repeatation(text):
    """
    This Function will reduce repeatition to two characters 
    for alphabets and to one character for punctuations.
    
    arguments:
         input_text: "text" of type "String".
         
    return:
        value: Finally formatted text with alphabets repeating to 
        two characters & punctuations limited to one repeatition 
        
    Example:
    Input : Realllllllllyyyyy,        Greeeeaaaatttt   !!!!?....;;;;:)
    Output : Reallyy, Greeaatt !?.;:)
    
    """
    # Pattern matching for all case alphabets
    Pattern_alpha = re.compile(r"([A-Za-z])\1{1,}", re.DOTALL)
    
    # Limiting all the  repeatation to two characters.
    Formatted_text = Pattern_alpha.sub(r"\1\1", text) 
    
    # Pattern matching for all the punctuations that can occur
    Pattern_Punct = re.compile(r'([.,/#!$%^&*?;:{}=_`~()+-])\1{1,}')
    
    # Limiting punctuations in previously formatted string to only one.
    Combined_Formatted = Pattern_Punct.sub(r'\1', Formatted_text)
    
    # The below statement is replacing repeatation of spaces that occur more than two times with that of one occurrence.
    Final_Formatted = re.sub(' {2,}',' ', Combined_Formatted)
    return Final_Formatted

# The code for expanding contraction words
def expand_contractions(text):
    # import library

    # creating an empty list
    expanded_words = []    
    for word in text.split():
      # using contractions.fix to expand the shotened words
      expanded_words.append(contractions.fix(word)) 
    
    String_Of_tokens = ' '.join(expanded_words)
    return String_Of_tokens

# The code for removing special characters
def removing_special_characters(text):
    pattern = r"[^a-zA-Z]"
    text = re.sub(pattern,' ', text)
    return text

def stemming_stopwords_meaninglessWords(text):
    port_stemmer = PorterStemmer()
    tokens = word_tokenize(text)
    text = ' '.join([port_stemmer.stem(token) for token in tokens if token not in stop and singularize(token) in words])
    return text

# remove stopwords, remove meaninglessWords -> No stemming
import nltk
#from pattern.text.en import singularize
words = set(nltk.corpus.words.words())

def remove_stopwords_meaninglessWords(text):
    stop = stopwords.words('english')
    words = set(nltk.corpus.words.words())
    tokens = word_tokenize(text)
    text = ' '.join([token for token in tokens if token not in stop and token in words])
    return text


# Text cleaning

def text_clean(text):

    text = text.lower()
    
    text = remove_links(text)
    
    text = expand_contractions(text)
    
    text = accented_characters_removal(text)
    
    text = removing_special_characters(text)

    text = reducing_incorrect_character_repeatation(text)
    
    text = remove_stopwords_meaninglessWords(text)
    
    return text
