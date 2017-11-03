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
import {GoogleSignin, GoogleSigninButton} from 'react-native-google-signin';

import CameraScreen from './CameraScreen';
import Api from './Api'

export default class App extends Component {
  
  constructor(props) {
    super(props);
    this.state = {
      app: undefined,
      email: null,
      kiekot: []
    };
  }

  componentDidMount() {
    this._setupGoogleSignin();
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
        {this.state.email ? this.logout() : this.login()}
        <View style={styles.container}>
          <TouchableOpacity onPress={() => this.setState({app: CameraScreen})}>
            <Text style={styles.buttonText}>
              Ota kuva
            </Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => this.getDiscs()} disabled={!this.state.email} >
            <Text style={styles.buttonText}>
              Hae kiekot
            </Text>
          </TouchableOpacity>
          {this.state.kiekot.map(k => <DiscRow kiekko={k} key={k.id} />)}
        </View>
      </View>
    );
  }

  getDiscs() {
    try {
      Api.getRaw(`kiekot/email?email=${this.state.email}`)
      .then((response) => {
        this.setState({
          app: undefined, 
          kiekot: R.pathOr([], ['kiekot'], response)
        })
      })
    }
    catch (e) {
      Alert.alert('Error', e.message)
    }
  }

  async _setupGoogleSignin() {
    try {
      await GoogleSignin.hasPlayServices({ autoResolve: true });
      await GoogleSignin.configure({
        webClientId: '107543052765-lfgp4lke6h51a0l4kp258anilpeegf8v.apps.googleusercontent.com',
        offlineAccess: false
      });

      const user = await GoogleSignin.currentUserAsync();
      console.log(user);
      this.setState({...this.state, email: user.email});
    }
    catch(err) {
      console.log("Play services error", err.code, err.message);
    }
  }

  _signIn() {
    GoogleSignin.signIn()
    .then((user) => {
      console.log(user);
      this.setState({...this.state, email: user.email});
    })
    .catch((err) => {
      console.log('WRONG SIGNIN', err);
    })
    .done();
  }

  _signOut() {
    GoogleSignin.revokeAccess().then(() => GoogleSignin.signOut()).then(() => {
      this.setState({...this.state, email: null});
    })
    .done();
  }

  login = () => (
    <View style={styles.container}>
      <GoogleSigninButton 
        style={{width: 120, height: 44}}
        color={GoogleSigninButton.Color.Light}
        size={GoogleSigninButton.Size.Icon}
        onPress={() => { this._signIn(); }} 
      />
    </View>
  )
  
  logout = () => (
    <View style={styles.container}>
      <Text style={styles.text}>
        {this.state.email}
      </Text>
      <TouchableOpacity onPress={() => this._signOut()}>
        <Text style={styles.buttonText}>
          Kirjaudu ulos
        </Text>
      </TouchableOpacity>
    </View>
  )
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
