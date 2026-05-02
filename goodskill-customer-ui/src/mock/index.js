// Mock配置文件
import Mock from 'mockjs'
import userMock from './user'
import goodsMock from './goods'
import orderMock from './order'

// 注册mock数据
const mocks = [...userMock, ...goodsMock, ...orderMock]

mocks.forEach(mock => {
  Mock.mock(mock.url, mock.method, mock.response)
})

export default Mock
