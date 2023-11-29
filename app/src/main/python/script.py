import cv2
import numpy as np

def calculate_cobb_angle(image_path):
    # Read the image
    image = cv2.imread(image_path)

    # Convert the image to grayscale
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Apply GaussianBlur to reduce noise
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)

    # Perform edge detection using Canny
    edges = cv2.Canny(blurred, 50, 150)

    # Find contours in the edged image
    contours, _ = cv2.findContours(edges.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # Filter out small contours
    contours = [cnt for cnt in contours if cv2.contourArea(cnt) > 100]

    # Sort contours by their y-coordinate (top to bottom)
    contours.sort(key=lambda cnt: cv2.boundingRect(cnt)[1])

    # Extract the two longest contours (presumed to be the vertebral boundaries)
    if len(contours) >= 2:
        top_contour = contours[-1]
        bottom_contour = contours[-2]

        # Fit bounding boxes to the contours
        _, _, w1, h1 = cv2.boundingRect(top_contour)
        _, _, w2, h2 = cv2.boundingRect(bottom_contour)

        # Calculate Cobb angle
        cobb_angle_rad = np.arctan((h2 - h1) / (w2 + w1))
        cobb_angle_deg = np.degrees(cobb_angle_rad)

        return cobb_angle_deg

    else:
        print("Could not find enough contours.")
        return 0