import mss
import mss.tools
import numpy as np
import cv2
import threading
import imutils


class Targeting():

    def __init__(self):
        print("Targeting starting.")

    def loadImage(self):
        from PIL import Image
        im = cv2.imread("./2020FRCRed.png")
        self.processImage(im)

    def processImage(self, img):
        grayImage = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        (thresh, blackAndWhiteImage) = cv2.threshold(grayImage, 125, 255, cv2.THRESH_BINARY)
        edges = cv2.Canny(blackAndWhiteImage, 50, 150, apertureSize=3)
        blurred = cv2.GaussianBlur(grayImage, (5, 5), 0)
        thresh = cv2.threshold(blurred, 60, 255, cv2.THRESH_BINARY)[1]

        imagem = cv2.bitwise_not(edges)

        cnts = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)

        for c in cnts:
            cv2.drawContours(img, [c], -1, (0, 255, 0), 2)

        minLineLength = 10
        maxLineGap = 5

        lines = cv2.HoughLinesP(edges, 1, np.pi / 180, 100, minLineLength, maxLineGap)

        for x in lines:
            #print(x)

            for x1, y1, x2, y2 in x:
                cv2.line(img, (x1, y1), (x2, y2), (255, 0, 0), 5)
                cv2.line(imagem, (x1, y1), (x2, y2), (0, 0, 0), 5)


        cv2.imshow('imagebw', grayImage)
        cv2.imshow('Black white image', blackAndWhiteImage)
        cv2.imshow('edge', edges)
        cv2.imshow('invert', imagem)
        cv2.imshow('image', img)
        cv2.imshow('blur',blurred)
        cv2.imshow('blurbw', thresh)

        cv2.waitKey(0)
        cv2.destroyAllWindows()


    def testScreemGrab(self):
        with mss.mss() as sct:
            # The screen part to capture
            monitor = {"top": 160, "left": 160, "width": 160, "height": 135}
            output = "sct-{top}x{left}_{width}x{height}.png".format(**monitor)

            # Grab the data
            sct_img = sct.grab(monitor)

            # Save to the picture file
            mss.tools.to_png(sct_img.rgb, sct_img.size, output=output)
            print(output)
