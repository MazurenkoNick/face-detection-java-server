![image](https://github.com/user-attachments/assets/8fdb8d34-5010-4f1d-b083-8f05e6884b2b)

# Face Detection 

## Java Server
Accepts requests from the front-end side via REST API.
To validate the image it acts as gRPC client to send the image in chunks using client-side streaming. 
If the image is valid, it'll be persisted either in a local file storage or database depending on the configuration in `application.properties` file.
The users can then download the successfully validated image.

## Python Server
Acts as gRPC server and accepts image in chunks, then validates it:
- Bounding box takes over 15% of the area of the photo.
- If a face is too small it's not good for a document.
- If Python face detector does not detect anything, the photo is not valid.
- If Python face detector detects more than one face, the photo is not valid.
- If there is only one face, the photo is valid
