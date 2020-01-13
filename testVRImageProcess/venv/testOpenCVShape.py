import cv2
import numpy as np

import mss
import mss.tools

import colorsys

frameWidth = 640
frameHeight = 480
cap = cv2.VideoCapture(0)
cap.set(3, frameWidth)
cap.set(4, frameHeight)

def empty(a):
    pass

cv2.namedWindow("Parameters")
cv2.resizeWindow("Parameters",640,500)
cv2.createTrackbar("Threshold1","Parameters",157,255,empty)
cv2.createTrackbar("Threshold2","Parameters",158,255,empty)
cv2.createTrackbar("Area","Parameters",4600,30000,empty)
cv2.createTrackbar("AreaHigh","Parameters",15000,30000,empty)
cv2.createTrackbar("Blur1","Parameters",3,7,empty)
cv2.createTrackbar("Blur2","Parameters",3,7,empty)
cv2.createTrackbar("Blur3","Parameters",0,10,empty)

cv2.createTrackbar("bw1","Parameters",66,255,empty)
cv2.createTrackbar("bw2","Parameters",255,255,empty)

#Color filter
cv2.namedWindow("ColorPar")
cv2.resizeWindow("ColorPar", 640, 240)
cv2.createTrackbar("LHSV1","ColorPar",0,179,empty)
cv2.createTrackbar("LHSV2","ColorPar",0,255,empty)
cv2.createTrackbar("LHSV3","ColorPar",0,255,empty)
cv2.createTrackbar("HHSV1","ColorPar",0,179,empty)
cv2.createTrackbar("HHSV2","ColorPar",0,255,empty)
cv2.createTrackbar("HHSV3","ColorPar",0,255,empty)

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

def getContours(img,imgContour):
    contours, hierarchy = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    for cnt in contours:
        area = cv2.contourArea(cnt)
        areaMin = cv2.getTrackbarPos("Area", "Parameters")
        areaMax = cv2.getTrackbarPos("AreaHigh", "Parameters")
        if area > areaMin and area < areaMax:
            cv2.drawContours(imgContour, cnt, -1, (255, 0, 255), 7)
            peri = cv2.arcLength(cnt, True)
            approx = cv2.approxPolyDP(cnt, 0.02 * peri, True)
            print(len(approx))
            x , y , w, h = cv2.boundingRect(approx)
            cv2.rectangle(imgContour, (x , y ), (x + w , y + h ), (0, 255, 0), 5)

            cv2.putText(imgContour, "Points: " + str(len(approx)), (x + w + 20, y + 20), cv2.FONT_HERSHEY_COMPLEX, .7,
                        (0, 255, 0), 2)
            cv2.putText(imgContour, "Area: " + str(int(area)), (x + w + 20, y + 45), cv2.FONT_HERSHEY_COMPLEX, 0.7,
                        (0, 255, 0), 2)

def testScreemGrab():
    with mss.mss() as sct:
        # The screen part to capture
        monitor = {"top": 160, "left": 160, "width": 640, "height": 480}
        output = "sct-{top}x{left}_{width}x{height}.png".format(**monitor)

        # Grab the data
        sct_img = sct.grab(monitor)
        img = np.array(sct_img)
        # Save to the picture file
        #mss.tools.to_png(sct_img.rgb, sct_img.size, output=output)
        return img

def colorFilter(img):
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    lowerColor = np.array(([
        cv2.getTrackbarPos("LHSV1", "ColorPar")
        , cv2.getTrackbarPos("LHSV2", "ColorPar")
        , cv2.getTrackbarPos("LHSV3", "ColorPar")
    ]))

    highColor = np.array(([
        cv2.getTrackbarPos("HHSV1", "ColorPar")
        , cv2.getTrackbarPos("HHSV2", "ColorPar")
        , cv2.getTrackbarPos("HHSV3", "ColorPar")
    ]))



    mask = cv2.inRange(hsv, lowerColor, highColor)
    result = cv2.bitwise_and(img, img, mask = mask)

    low = colorsys.hsv_to_rgb(
        cv2.getTrackbarPos("LHSV1", "ColorPar")
        , cv2.getTrackbarPos("LHSV2", "ColorPar")
        , cv2.getTrackbarPos("LHSV3", "ColorPar")
    )

    high = colorsys.hsv_to_rgb(
        cv2.getTrackbarPos("HHSV1", "ColorPar")
        , cv2.getTrackbarPos("HHSV2", "ColorPar")
        , cv2.getTrackbarPos("HHSV3", "ColorPar")
    )

    cv2.rectangle(result, (0,0), (30,30), low, 10)
    cv2.rectangle(result, (30, 0), (30, 30), high, 10)

    return result

while True:
    #success, img = cap.read()
    #img = cv2.imread("./2020FRCRed.png")
    img = testScreemGrab()

    imgContour = img.copy()

    blurThreshold1 = cv2.getTrackbarPos("Blur1", "Parameters")
    blurThreshold2 = cv2.getTrackbarPos("Blur2", "Parameters")
    blurThreshold3 = cv2.getTrackbarPos("Blur3", "Parameters")

    bw1 = cv2.getTrackbarPos("bw1", "Parameters")
    bw2 = cv2.getTrackbarPos("bw2", "Parameters")



    imgBlur = cv2.GaussianBlur(img, (7, 7), blurThreshold3)
    imgGray = cv2.cvtColor(imgBlur, cv2.COLOR_BGR2GRAY)
    imagem = cv2.bitwise_not(imgGray)

    (thresh, blackAndWhiteImage) = cv2.threshold(imgGray, bw1, bw2, cv2.THRESH_BINARY)
    bwImgBlur = cv2.GaussianBlur(blackAndWhiteImage, (7, 7), blurThreshold3)

    threshold1 = cv2.getTrackbarPos("Threshold1", "Parameters")
    threshold2 = cv2.getTrackbarPos("Threshold2", "Parameters")
    imgCanny = cv2.Canny(imgGray,threshold1,threshold2)
    imgCanny2 = cv2.Canny(imagem,threshold1,threshold2)
    imgCannyBW = cv2.Canny(bwImgBlur,threshold1,threshold2)

    kernel = np.ones((5, 5))
    imgDil = cv2.dilate(imgCanny, kernel, iterations=1)
    imgDil2 = cv2.dilate(imgCanny2, kernel, iterations=1)
    imgDilBW = cv2.dilate(imgCannyBW, kernel, iterations=1)
    getContours(imgDil,imgContour)
    getContours(imgDilBW, img)

    #imgStack = stackImages(0.2,([img,imgCanny],[imgDil,imgContour]))
    #cv2.imshow("Result", imgStack)
    cv2.imshow("Canny", imgCanny)
    cv2.imshow("Final", imgContour)
    cv2.imshow("FinalBW", img)
    cv2.imshow("BWCanny", imgCannyBW)

    #Color Filter
    cv2.imshow("Color Filter", colorFilter(img))

    cv2.imshow("BlackWhite", blackAndWhiteImage)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break