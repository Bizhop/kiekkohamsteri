import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Alert
} from 'react-native';
import { CameraKitCamera } from 'react-native-camera-kit';

import CameraScreen from './CameraScreen';

export default class App extends Component {
  
  constructor(props) {
    super(props);
    this.state = {
      app: undefined
    };
  }

  render() {
    if (this.state.app) {
      const App = this.state.app;
      return <App />;
    }
    return (
      <View style={{flex: 1}}>
        <View style={styles.headerContainer}>
          <Text style={styles.headerText}>
            Kiekkohamsteri
          </Text>
        </View>
        <View style={styles.container}>
          <TouchableOpacity onPress={() => this.setState({app: CameraScreen})}>
              <Text style={styles.buttonText}>
                Ota kuva
              </Text>
            </TouchableOpacity>
        </View>
      </View>
    );
  }

}



const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  headerContainer: {
    flexDirection: 'column',
    backgroundColor: '#F5FCFF',
    justifyContent: 'center',
    alignItems: 'center',
    paddingTop: 100
  },
  headerText: {
    color: 'black',
    fontSize: 24
  },
  buttonText: {
    color: 'blue',
    marginBottom: 20,
    fontSize: 20
  }
});
