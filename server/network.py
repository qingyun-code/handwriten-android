import torch
import torch.nn as nn

# 自定义手写数字识别网络
class net(nn.Module):
  def __init__(self):
    super(net, self).__init__()

    self.Conn_layers = nn.Sequential(
        nn.Linear(784, 100),
        nn.Sigmoid(),
        nn.Linear(100, 10),
        nn.Sigmoid()
      )

  def forward(self, input):
    output = self.Conn_layers(input)

    return output