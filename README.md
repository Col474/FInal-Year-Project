# Final Year Project
This is a personal wardrobe organiser/outfit generator app. The app ueses an ML model (MobileNetV2) trained on a modified version of the DeepFashion database to categorize clothing images. The codelab used to train the model can be found here: https://colab.research.google.com/drive/1cGvpHMu-AElc3jjf-xaTiZzj29QX2weE#scrollTo=qNokDnTiVV3g

The app consists of 4 sections:

1. Add New Items: Items are added by photographing the item using the device camera. The image is classified using the tf model and clothing colour retrieved from the image.
2. View Wardrobe: Items are dynamically added and sorted by cagtegory once they are saved (Hold an item to delete). Items are stored using Room with SQLite
3. Outfit Generator: Weather info is obtained via AccuWeather APIs and the outfit is generated based on this data and using MSE to compute colour similarity. Generated outfits can be saved.
5. Saved Outfits: Saved outfits can be viewed/deleted.
