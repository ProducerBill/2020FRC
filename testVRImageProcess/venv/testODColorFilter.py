import numpy as np
import cv2
import mss
import mss.tools
import PIL as Image

import scipy.spatial as sp
import matplotlib.pyplot as plt

def empty(a):
    pass


cv2.namedWindow("Parameters")
cv2.resizeWindow("Parameters",640,500)
cv2.createTrackbar("Threshold1","Parameters",157,255,empty)
cv2.createTrackbar("Threshold2","Parameters",158,255,empty)
cv2.createTrackbar("Area","Parameters",2000,30000,empty)
cv2.createTrackbar("AreaHigh","Parameters",60000,60000,empty)
cv2.createTrackbar("Blur1","Parameters",3,7,empty)
cv2.createTrackbar("Blur2","Parameters",3,7,empty)
cv2.createTrackbar("Blur3","Parameters",0,10,empty)
cv2.createTrackbar("bw1","Parameters",66,255,empty)
cv2.createTrackbar("bw2","Parameters",255,255,empty)

cv2.namedWindow("HSV")
cv2.resizeWindow("HSV", 640, 480)
cv2.createTrackbar("LHSV1", "HSV", 0, 179, empty)
cv2.createTrackbar("LHSV2", "HSV", 41, 255, empty)
cv2.createTrackbar("LHSV3", "HSV", 171, 255, empty)
cv2.createTrackbar("HHSV1", "HSV", 8, 179, empty)
cv2.createTrackbar("HHSV2", "HSV", 162, 255, empty)
cv2.createTrackbar("HHSV3", "HSV", 255, 255, empty)

def stackImages(scale,imgArray):
    rows = len(imgArray)
    cols = len(imgArray[0])
    rowsAvailable = isinstance(imgArray[0], list)
    width = imgArray[0][0].shape[1]
    height = imgArray[0][0].shape[0]
    if rowsAvailable:
        for x in range ( 0, rows):
            for y in range(0, cols):
                if imgArray[x][y].shape[:2] == imgArray[0][0].shape [:2]:
                    imgArray[x][y] = cv2.resize(imgArray[x][y], (0, 0), None, scale, scale)
                else:
                    imgArray[x][y] = cv2.resize(imgArray[x][y], (imgArray[0][0].shape[1], imgArray[0][0].shape[0]), None, scale, scale)
                if len(imgArray[x][y].shape) == 2: imgArray[x][y]= cv2.cvtColor( imgArray[x][y], cv2.COLOR_GRAY2BGR)
        imageBlank = np.zeros((height, width, 3), np.uint8)
        hor = [imageBlank]*rows
        hor_con = [imageBlank]*rows
        for x in range(0, rows):
            hor[x] = np.hstack(imgArray[x])
        ver = np.vstack(hor)
    else:
        for x in range(0, rows):
            if imgArray[x].shape[:2] == imgArray[0].shape[:2]:
                imgArray[x] = cv2.resize(imgArray[x], (0, 0), None, scale, scale)
            else:
                imgArray[x] = cv2.resize(imgArray[x], (imgArray[0].shape[1], imgArray[0].shape[0]), None,scale, scale)
            if len(imgArray[x].shape) == 2: imgArray[x] = cv2.cvtColor(imgArray[x], cv2.COLOR_GRAY2BGR)
        hor= np.hstack(imgArray)
        ver = hor
    return ver

def screemGrab():
    with mss.mss() as sct:
        # The screen part to capture
        monitor = {"top": 160, "left": 160, "width": 640, "height": 480}

        # Grab the data
        sct_img = sct.grab(monitor)
        img = np.array(sct_img)
        return img

def filterColor(img, MaskColors): # lowerColor, highColor):
    #Converting the image to HSV
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    masks = []

    #Building the mask to find just the colors input.
    for msk in MaskColors:
        masks.append(cv2.inRange(hsv, msk[0], msk[1]))

    mask = None

    mask = sum(masks)

    #mask = cv2.inRange(hsv, lowerColor, highColor)

    #Bitwising the base image to the mask.
    result = cv2.bitwise_and(img, img, mask=mask)

    #Returning the results.
    return result



def findEdges(img, threshold1, threshold2):
    imgBlur = cv2.GaussianBlur(img, (7, 7), 2)
    imgGray = cv2.cvtColor(imgBlur, cv2.COLOR_BGR2GRAY)
    imgCanny = cv2.Canny(imgGray, threshold1, threshold2)

    return imgCanny

def getContours(img,imgContour, areaMin, areaMax):
    contours, hierarchy = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    cntResults = []

    for cnt in contours:
        area = cv2.contourArea(cnt)
        if area > areaMin and area < areaMax:
            cv2.drawContours(imgContour, cnt, -1, (255, 0, 255), 7)
            peri = cv2.arcLength(cnt, True)
            approx = cv2.approxPolyDP(cnt, 0.02 * peri, True)


            cntResults.append(cnt)



    return cntResults

def drawContours(imgTemp, contours):
    for cnt in contours:
        area = cv2.contourArea(cnt)
        cv2.drawContours(imgTemp, cnt, -1, (255, 0, 255), 7)
        peri = cv2.arcLength(cnt, True)
        approx = cv2.approxPolyDP(cnt, 0.02 * peri, True)
        x, y, w, h = cv2.boundingRect(approx)
        cv2.rectangle(imgTemp, (x, y), (x + w, y + h), (0, 255, 0), 5)

        cv2.putText(imgTemp, "Points: " + str(len(approx)), (x + w + 20, y + 20), cv2.FONT_HERSHEY_COMPLEX, .7,
                    (0, 255, 0), 2)
        cv2.putText(imgTemp, "Area: " + str(int(area)), (x + w + 20, y + 45), cv2.FONT_HERSHEY_COMPLEX, 0.7,
                    (0, 255, 0), 2)

    return imgTemp

def matchContourToMaster(contour, offset):
    for cnt in contour:
        for data in cnt:
            data[0][0] = data[0][0] + offset[1]
            data[0][1] = data[0][1] + offset[0]

    return contour

def findTargets(img):

    #Final target information
    lowerTargetData = None
    upperTargetData = None

    #Grabing the lower part of the image as the lower target is most likely in that area.
    height, width, channels = img.shape
    lowerImg = img[(int)(height / 2): height, 0: width]
    lowerImgLoc = [(int)(height/2), 0]

    cv2.imshow("Lower Half", lowerImg)

    #Real ranges Blue
    lTLowerHSV = np.array(([63, 43, 59]))
    lTUpperHSV = np.array(([111, 102, 187]))
    #maskColor = ([[lTLowerHSV, lTUpperHSV]])

    #Real ranges for Red
    maskColor = ([
        [np.array((([0, 50, 50]))), np.array(([8, 255, 255]))]
        ,[np.array((([170, 150, 130]))), np.array(([180, 255, 255]))]
    ])

    #fliter image for color.
    filterImg = filterColor(lowerImg, maskColor)

    #For filter debug
    cv2.imshow("First Filter", filterImg)

    #Finding the edges.
    #imgEdges = findEdges(filterImg, 157, 158) #For Blue
    imgEdges = findEdges(filterImg, 165, 106)
    cv2.imshow("Found Edges", imgEdges)

    kernel = np.ones((5, 5))
    imgDil = cv2.dilate(imgEdges, kernel, iterations=1)

    #Debug for dilation
    cv2.imshow("Dia", imgDil)

    #Making a copy of the image.
    imgP2 = img.copy()

    #Finding the contours
    LTContours = getContours(imgDil, img.copy(), 2000, 60000)

    #Updating the contours to match master image.
    LTContours = matchContourToMaster(LTContours, lowerImgLoc)

    imgFP = drawContours(img, LTContours )


    #If the lower target is found then look for upper.

    #Flitering all contours looking for the largest best one.
    if len(LTContours) > 0: # If there are any contours start looking for the upper.

        lTar = LTContours[0]

        for cnt in LTContours:
            areaCnt = cv2.contourArea(cnt)
            arealTar = cv2.contourArea(lTar)
            if areaCnt > arealTar:
                lTar = cnt

        area = cv2.contourArea(lTar)
        if area > 2000 and area < 60000:
            peri = cv2.arcLength(lTar, True)
            approx = cv2.approxPolyDP(lTar, 0.02 * peri, True)
            x, y, w, h = cv2.boundingRect(approx)
            #if len(approx) >= 8:
            if x > 0 and y > 0:
                upperImg = imgP2[0: y, x: x + w]
                cv2.imshow("UpperArea", upperImg)

                #Start of second tier processing.
                manualTune(upperImg)

                #HTContours = findUpperTarget(upperImg)
                #HTContours = matchContourToMaster(HTContours, ([0, x]))
                #imgFP = drawContours(imgFP, HTContours)

    cv2.imshow("Final", imgFP)


def findUpperTarget(upperImg):

    #Upper Mask Values Red Values
    hTLowerHSV = np.array(([0, 0, 0]))
    hTUpperHSV = np.array(([179, 255, 255]))
    maskColor2 = ([[hTLowerHSV, hTUpperHSV]])

    # fliter image for color.
    filterImg = filterColor(upperImg, maskColor2)

    # Finding the edges. Red Values
    imgEdges = findEdges(
        filterImg
        , 229 #cv2.getTrackbarPos("Threshold1", "Parameters")
        , 159 #cv2.getTrackbarPos("Threshold2", "Parameters")
    )

    cv2.imshow("UpperEdge", imgEdges)

    kernel = np.ones((5, 5))
    #imgErosion = cv2.erode(imgEdges, kernel, iterations=1)
    imgDil = cv2.dilate(imgEdges, kernel, iterations=1)

    cv2.imshow("UpperDil", imgDil)

    # Finding the contours
    HTContours = getContours(imgDil, upperImg.copy(), 2000, 60000)

    imgFP = drawContours(upperImg, HTContours)
    cv2.imshow("FinalHT", imgFP)

    return HTContours

def manualTune(img):
    imgContour = img.copy()

    colorFilter = filterColor(
        img
        , [([np.array(([cv2.getTrackbarPos("LHSV1", "HSV")
            , cv2.getTrackbarPos("LHSV2", "HSV")
            , cv2.getTrackbarPos("LHSV3", "HSV")]))
        , np.array(([cv2.getTrackbarPos("HHSV1", "HSV")
            , cv2.getTrackbarPos("HHSV2", "HSV")
            , cv2.getTrackbarPos("HHSV3", "HSV")]))
        ])]
    )


    imgCanny = findEdges(
        colorFilter
        , cv2.getTrackbarPos("Threshold1", "Parameters")
        , cv2.getTrackbarPos("Threshold2", "Parameters")
    )

    kernel = np.ones((5, 5))
    imgDil = cv2.dilate(imgCanny, kernel, iterations=1)

    areaMin = cv2.getTrackbarPos("Area", "Parameters")
    areaMax = cv2.getTrackbarPos("AreaHigh", "Parameters")

    cntList = getContours(imgDil, imgContour, areaMin, areaMax)

    imgContour = drawContours(img, cntList)


    cv2.imshow("Color Filter", colorFilter)

    cv2.imshow("Canny", imgCanny)
    cv2.imshow("Dil", imgDil)
    cv2.imshow("contour", imgContour)

    imgStack = stackImages(0.7, ([img, colorFilter]))
    cv2.imshow("Output", imgStack)



while True:
    img = screemGrab()

    #manualTune(img)
    findTargets(img)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
