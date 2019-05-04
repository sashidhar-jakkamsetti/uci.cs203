import cv2
import os
from subprocess import check_output as call
import math
import numpy as np
import sys

def print_status(in_string):
    print('\n==> Running ', end =" ")
    for element in in_string:
        print(element, end =" ")
        
    return 0;

def extract_minutiae(tarp_loc, mindtct_loc, in_img, out_img, out_minutiae):
    img = cv2.imread(in_img,0)
    rows,cols = img.shape
    
    run_tarp = ["java","-jar", tarp_loc, in_img]
    print_status(run_tarp)
    alignment = call(run_tarp).decode("utf-8").rstrip().split(" ")

    if len(alignment) != 3:
        # This means that the minutiae processing fails. The input image is terrible.
        return 1;

    alignment = [float(alignment[0]), float(alignment[1]), float(alignment[2])]
    angle = 360*(alignment[2]/(2*math.pi))

    M = np.float32([[1, 0, 150 - alignment[0]], [0, 1, 150 - alignment[1]]])
    dst = cv2.warpAffine(img, M, (cols, rows))

    M = cv2.getRotationMatrix2D((cols/2, rows/2), angle, 1)
    dst2 = cv2.warpAffine(dst, M, (cols, rows))
    cv2.imwrite(out_img, dst2)
    
    run_mindtct = [mindtct_loc, out_img, out_minutiae]
    print_status(run_mindtct)
    call(run_mindtct)
    
    print("\n\nMinutiae extraction completed!", end='\n')
    print("Please check %s.xyt for minutiae.\n" % out_minutiae)

    return 0;

extract_minutiae(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5]);
