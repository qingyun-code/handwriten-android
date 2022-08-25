import cv2
import numpy as np

def image_preprocessing():

	# 读取图片
	img = cv2.imread("getImage/image.jpg")

	# =====================图像处理======================== #

	# 转换成灰度图像
	gray_img = cv2.cvtColor(img , cv2.COLOR_BGR2GRAY)

	# 进行高斯滤波
	gauss_img = cv2.GaussianBlur(gray_img, (5,5), 0, 0, cv2.BORDER_DEFAULT)

	# 边缘检测
	img_edge1 = cv2.Canny(gauss_img, 100, 200)

	# =====================图像分割======================== #

	# 获取原始图像的宽和高
	high = img.shape[0]
	width = img.shape[1]

	# 分别初始化高和宽的和
	add_width = np.zeros(high, dtype = int)
	add_high = np.zeros(width, dtype = int)

	# 计算每一行的灰度图的值的和
	for h in range(high):
		for w in range(width):
			add_width[h] = add_width[h] + img_edge1[h][w]

	# 计算每一列的值的和
	for w in range(width):
		for h in range(high):
			add_high[w] = add_high[w] + img_edge1[h][w]

	# 初始化上下边界为宽度总值最大的值的索引
	acount_high_up = np.argmax(add_width)
	acount_high_down = np.argmax(add_width)

	# 将上边界坐标值上移，直到没有遇到白色点停止，此为数字的上边界
	while add_width[acount_high_up] != 0:
		acount_high_up = acount_high_up + 1

	# 将下边界坐标值下移，直到没有遇到白色点停止，此为数字的下边界
	while add_width[acount_high_down] != 0:
		acount_high_down = acount_high_down - 1

	# 初始化左右边界为宽度总值最大的值的索引
	acount_width_left = np.argmax(add_high)
	acount_width_right = np.argmax(add_high)

	# 将左边界坐标值左移，直到没有遇到白色点停止，此为数字的左边界
	while add_high[acount_width_left] != 0:
		acount_width_left = acount_width_left - 1

	# 将右边界坐标值右移，直到没有遇到白色点停止，此为数字的右边界
	while add_high[acount_width_right] != 0:
		acount_width_right = acount_width_right + 1

	# 求出宽和高的间距
	width_spacing = acount_width_right - acount_width_left
	high_spacing = acount_high_up - acount_high_down

	# 求出宽和高的间距差
	poor = width_spacing - high_spacing

	# 将数字进行正方形分割，目的是方便之后进行图像压缩
	if poor > 0:
		tailor_image = img[acount_high_down - poor \
		// 2 - 5:acount_high_up + poor - poor \
		// 2 + 5, acount_width_left - 5:acount_width_right + 5]
	else:
		tailor_image = img[acount_high_down - 5:acount_high_up + 5, \
		acount_width_left + poor // \
		2 - 5:acount_width_right - poor + poor // 2 + 5]

	# ======================小图处理======================= #

	# 将裁剪后的图片进行灰度化
	gray_img = cv2.cvtColor(tailor_image , cv2.COLOR_BGR2GRAY)

	# 高斯去噪
	gauss_img = cv2.GaussianBlur(gray_img, (5,5), 0, 0, cv2.BORDER_DEFAULT)

	# 将图像形状调整到28*28大小
	zoom_image = cv2.resize(gauss_img, (28, 28))

	# 获取图像的高和宽
	high = zoom_image.shape[0]
	wide = zoom_image.shape[1]

	# 将图像每个点的灰度值进行阈值比较
	for h in range(high):
		for w in range(wide):

			# 若灰度值大于100，则判断为背景并赋值0，否则将深灰度值变白处理
			if zoom_image[h][w] > 100:
				zoom_image[h][w] = 0
			else:
				zoom_image[h][w] = 255 - zoom_image[h][w]

	return zoom_image