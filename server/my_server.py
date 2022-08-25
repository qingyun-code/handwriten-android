from flask import Flask
from flask import request
import os
from werkzeug.utils import secure_filename
from predict import *

app = Flask(__name__)

@app.route('/')
def test():
	return '服务器正常运行'

# 此方法接收图片
@app.route('/upload', methods=['POST'])
def upload():

	f = request.files['file']
	print('连接成功')

	# 当前文件所在路径
	basepath = os.path.dirname(__file__)
	upload_path = os.path.join(basepath, 'getImage', secure_filename(f.filename))

	# 保存文件
	f.save(upload_path)

	# 放入预测函数得到结果
	my_result = predict_number()
	print(my_result)

	# 将结果返回给手机
	return str(my_result)

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5555)