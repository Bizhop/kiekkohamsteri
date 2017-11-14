import { StackNavigator } from 'react-navigation'
import HomeScreen from './HomeScreen'
import CameraScreen from './CameraScreen'

const App = StackNavigator({
    Home: { screen: HomeScreen },
    Camera: { screen: CameraScreen }
})

export default App
