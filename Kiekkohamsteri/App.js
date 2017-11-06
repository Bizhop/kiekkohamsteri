import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Alert,
  Image
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
      user: null,
      kiekot: [],
      backendUp: false,
      loggedIn: false,
    };
  }

  componentDidMount() {
    this._setupGoogleSignin();
    this.pingBackend();
  }

  pingBackend() {
    setTimeout(() => this.pingBackend(), 30000)
    try {
      Api.ping()
      .then((response) => {
        this.setState({
          ...this.state,
          backendUp: true
        })
      })
    }
    catch (e) {
      Alert.alert('Error', e.message)
    }
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
          {this.state.backendUp ? 
            <Image source={require('./images/green_light.png')} style={styles.icon} /> :
            <Image source={require('./images/red_light.png')} style={styles.icon} />
          }
        </View>
        {this.state.user ? this.showLogout() : this.showLogin()}
        {this.state.loggedIn && this.showButtons()}
      </View>
    );
  }

  showButtons = () => (
    <View style={styles.container}>
      <TouchableOpacity onPress={() => this.setState({app: CameraScreen})}>
        <Text style={styles.buttonText}>
          Ota kuva
        </Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => this.getDiscs()} >
        <Text style={styles.buttonText}>
          Hae kiekot
        </Text>
      </TouchableOpacity>
      {this.state.kiekot.map(k => <DiscRow kiekko={k} key={k.id} />)}
    </View>
  )

  getDiscs() {
    try {
      Api.get('kiekot', this.state.user)
      .then((response) => {
        this.setState({
          ...this.state,
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
      this.setState({...this.state, user: user, loggedIn: Api.login(user)});
    }
    catch(err) {
      console.log("Play services error", err.code, err.message);
    }
  }

  _signIn() {
    GoogleSignin.signIn()
    .then((user) => {
      console.log(user);
      this.setState({...this.state, user: user, loggedIn: Api.login(user)});
    })
    .catch((err) => {
      console.log('WRONG SIGNIN', err);
    })
    .done();
  }

  _signOut() {
    GoogleSignin.revokeAccess().then(() => GoogleSignin.signOut()).then(() => {
      this.setState({...this.state, user: null, loggedIn: false});
    })
    .done();
  }

  showLogin = () => (
    <View style={styles.container}>
      <GoogleSigninButton 
        style={{width: 120, height: 44}}
        color={GoogleSigninButton.Color.Light}
        size={GoogleSigninButton.Size.Icon}
        onPress={() => { this._signIn(); }} 
      />
    </View>
  )
  
  showLogout = () => (
    <View style={styles.container}>
      <Text style={styles.text}>
        {this.state.user.givenName} {this.state.user.familyName}: ({this.state.user.email})
      </Text>
      <Text style={styles.text}>
        {this.state.loggedIn ? 'Kirjautunut hamsteriin' : 'Ei kirjautunut hamsteriin'}
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
    {props.kiekko.muovi} {props.kiekko.mold} {props.kiekko.paino}g ({props.kiekko.vari}) {props.kiekko.kunto}/10
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
    flexDirection: 'row',
    flexWrap: 'wrap',
    backgroundColor: '#F5FCFF',
    justifyContent: 'center',
    alignItems: 'center',
    paddingTop: 50
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
  },
  icon: {
    width: 24,
    height: 24
  }
});
