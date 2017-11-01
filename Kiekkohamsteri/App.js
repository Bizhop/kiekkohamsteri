import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Alert
} from 'react-native';
import { CameraKitCamera } from 'react-native-camera-kit';
import R from 'ramda'

import CameraScreen from './CameraScreen';
import Api from './Api'

export default class App extends Component {
  
  constructor(props) {
    super(props);
    this.state = {
      app: undefined,
      kiekot: [{id: 1, mold: "testi"}]
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
          <TouchableOpacity onPress={() => this.getDiscs()}>
            <Text style={styles.buttonText}>
              Hae Villen kiekot
            </Text>
          </TouchableOpacity>
          {this.state.kiekot.map(k => <DiscRow kiekko={k} key={k.id} />)}
        </View>
      </View>
    );
  }
  getDiscs() {
    try {
      const response = Api.getRaw('kiekot/email?email=ville.piispa@gmail.com')
      Alert.alert('Villen kiekot:', `${response.kiekot}`)
      this.setState({
        app: undefined, 
        kiekot: R.pathOr([], ['kiekot'], response)
      })
    }
    catch (e) {
      Alert.alert('Error', e.message)
    }
  }
}

const DiscRow = props => (
  <Text style={styles.text}>
    {props.kiekko.mold}
  </Text>
)

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
  },
  text: {
    color: 'black',
    fontSize: 15
  }
});
