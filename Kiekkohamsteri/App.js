import { StackNavigator } from 'react-navigation'
import HomeScreen from './HomeScreen'
import CameraScreen from './CameraScreen'
import ListaScreen from './ListaScreen'
import KiekkoScreen from './KiekkoScreen'

const App = StackNavigator({
  Home: { screen: HomeScreen },
  Camera: { screen: CameraScreen },
  Lista: { screen: ListaScreen },
  Kiekko: { screen: KiekkoScreen },
})

export default App
