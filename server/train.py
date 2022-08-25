import torch
import torch.nn as nn
import torch.optim as optim
from torchvision import datasets, transforms
from torch.autograd import Variable
from torch.utils.data import DataLoader

from network import *

# 下载训练集
train_dataset = datasets.MNIST(root='./data/',
        train=True,
        transform=transforms.ToTensor(),
        download=False)
# 下载测试集
test_dataset = datasets.MNIST(root='./data/',
        train=False,
        transform=transforms.ToTensor(),
        download=False)

# 设置批次数
batch_size = 100

# 装载训练集
train_loader = torch.utils.data.DataLoader(dataset = train_dataset,
            batch_size = batch_size,
            shuffle=True)
# 装载测试集
test_loader = torch.utils.data.DataLoader(dataset = test_dataset,
            batch_size = batch_size,
            shuffle = True)

# 定义学习率
LR = 0.1

# 定义一个网络对象
net = net()

# 损失函数使用交叉熵
loss_function = nn.CrossEntropyLoss()

# 优化函数使用 SGD
optimizer = optim.SGD(
  net.parameters(),
  lr = LR,
  momentum = 0.9,
  weight_decay = 0.0005
)

# 定义迭代次数
epoch = 20

# 进行迭代训练
for epoch in range(epoch):
  for i, data in enumerate(train_loader):
    inputs, labels = data
    
    # 转换下输入形状
    inputs = inputs.reshape(batch_size, 784)

    inputs, labels = Variable(inputs), Variable(labels)
    outputs = net(inputs)
    loss = loss_function(outputs, labels)
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()
  
  # 初始化正确结果数为0
  test_result = 0
  
  # 用测试数据进行测试
  for data_test in test_loader:
    images, labels = data_test
    
    # 转换下输入形状
    images = images.reshape(batch_size, 784)

    images, labels = Variable(images), Variable(labels)
    output_test = net(images)
    
    # 对一个批次的数据的准确性进行判断
    for i in range(len(labels)):
    
      # 如果输出结果的最大值的索引与标签内正确数据相等，准确个数累加
      if torch.argmax(output_test[i]) == labels[i]:
        test_result += 1
        
  # 打印每次迭代后正确的结果数
  print("Epoch {} : {} / {}".format(epoch, test_result, len(test_dataset)))
  
# 保存权重模型
torch.save(net.state_dict(), 'weight/test.pth')