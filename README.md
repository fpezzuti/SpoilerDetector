# SpoilerDetector
_Spoiler Detector_ is a Java application that allows users to search for movie reviews avoding to spoilers about the plot. To this purpose, the application exploits a **binary classifier** that classifies movie reviews as *spoiler* and *not spoiler*.

_Spoiler Detector_ has been developed for the final project of the course of _Data Mining and Machine Learning_ of the University of Pisa by:
- [Lorenzo Massagli](https://github.com/ImBadnick/imbadnick.github.io)
- [Francesca Pezzuti](https://github.com/fpezzuti)

More details about the project can be found in:
- the [documentation](./SpoilerDetectorDocumentation.pdf)
- the [presentation](./SpoilerDetectorPresentation.pdf)


## Our workflow
The **worklow** followed is reported in the following diagram:

![Workflow](https://github.com/fpezzuti/SpoilerDetector/assets/75533556/f475a9bc-bac2-45b7-bacb-036210bf1f05)

## Dataset \& Data analysis
The **dataset** used is available on Kaggle at the following link:
- [imb-spoiler-dataset](https://www.kaggle.com/rmisra/imdb-spoiler-dataset)

The dataset contains 573913 reviews from 1998 to 2018 about 1572 movies and its volume is of 976MB.


Some statistics we collected about the dataset are reported in the following graphs:

![data analysis](https://github.com/fpezzuti/SpoilerDetector/assets/75533556/8583fe41-ca5e-4e5d-a7a9-e8536c08dcb3)


## Jupyter notebook
The Jupyter notebook containing both the **data analysis** and the **classifier** is available [here](./SpoilerDetector.ipynb).

## Results

The results we obtained are reported in the following table:

| | Precision | Recall |
| --- | --- | --- |
| Multinomial | 0.62 | 0.13 |
| SVM | 0.56 | 0.36 |
| Logistic | 0.60 | 0.34 |

The model giving us the best performances was the Logistic. Hence, we decided to tune its parameters by varying the ngram range and trying to remove the stemming.

The deployed model has the following performances:

![deployed model's performances](https://github.com/fpezzuti/SpoilerDetector/assets/75533556/599c5d4e-1009-4293-a45f-34b78ccdc2c6)


## Demo Java application

Here is an overview of how a review containing a spoiler is shown to the user; by clicking on button `Spoiler content`, the textual content of the review will be displayed to the user.

![Spoiler alert and spoiler revealed](https://github.com/fpezzuti/SpoilerDetector/assets/75533556/17e1438b-49bb-4d44-844c-0a6dbb226bbe)
