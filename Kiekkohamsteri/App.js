import { StackNavigator } from 'react-navigation'
import HomeScreen from './HomeScreen'
import CameraScreen from './CameraScreen'
import ListaScreen from './ListaScreen'
import KiekkoScreen from './KiekkoScreen'
import UserScreen from './UserScreen'

const App = StackNavigator({
  Home: { screen: HomeScreen },
  Camera: { screen: CameraScreen },
  Lista: { screen: ListaScreen },
  Kiekko: { screen: KiekkoScreen },
  User: { screen: UserScreen },
})

export default App
