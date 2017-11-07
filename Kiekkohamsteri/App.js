import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Alert,
  Image,
  Modal
} from 'react-native';
import { CameraKitCamera } from 'react-native-camera-kit';
import R from 'ramda'
import {GoogleSignin, GoogleSigninButton} from 'react-native-google-signin';

import CameraScreen from './CameraScreen';
import Api from './Api'

const imagesUrl = 'https://res.cloudinary.com/djc4j4dcs/'

export default class App extends Component {
  
  constructor(props) {
    super(props);
    this.state = {
      app: undefined,
      user: null,
      kiekot: [],
      backendUp: false,
      modalVisible: false,
      selectedDisc: null,
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
        <Modal
          animationType="slide"
          transparent={false}
          visible={this.state.modalVisible}
          onRequestClose={() => {
            this.setState({
              ...this.state,
              modalVisible: false,
            })
          }}
          >
            {this.state.selectedDisc ? (
              <View style={styles.container}>
                <Text>{discBasics(this.state.selectedDisc)}</Text>
                <Text>{discStats(this.state.selectedDisc)}</Text>
                <Image 
                  source={{uri: `${imagesUrl}${this.state.selectedDisc.kuva}`}} 
                  style={styles.discImage}
                />
              </View>
            ) : (
              <View style={styles.container}>
                <Text>Ei valittua kiekkoa</Text>
              </View>
            )}
        </Modal>
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
        {this.state.user && this.showButtons()}
      </View>
    );
  }

  showButtons = () => (
    <View style={styles.container}>
      <TouchableOpacity onPress={() => this.getDiscs()} >
        <Text style={styles.buttonText}>
          Hae kiekot
        </Text>
      </TouchableOpacity>
      {this.state.kiekot.map(k => this.discRow(k))}
    </View>
  )

  getDiscs() {
    try {
      Api.get('kiekot', this.state.user)
      .then((response) => {
        this.setState({
          ...this.state,
          kiekot: R.pathOr([], ['kiekot'], response)
        })
      })
    }
    catch (e) {
      console.log(e.message)
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
      if(user) {
        Api.login(user)
        this.setState({...this.state, user: user});
      }
    }
    catch(err) {
      console.log("Play services error", err.code, err.message);
    }
  }

  _signIn() {
    GoogleSignin.signIn()
    .then((user) => {
      console.log(user);
      Api.login(user)
      this.setState({...this.state, user: user});
    })
    .catch((err) => {
      console.log('WRONG SIGNIN', err);
    })
    .done();
  }

  _signOut() {
    GoogleSignin.revokeAccess().then(() => GoogleSignin.signOut()).then(() => {
      this.setState({...this.state, user: null});
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
      <TouchableOpacity onPress={() => this._signOut()}>
        <Text style={styles.buttonText}>
          Kirjaudu ulos
        </Text>
      </TouchableOpacity>
    </View>
  )

  discRow = kiekko => (
    <View key={kiekko.id}>
      <TouchableOpacity onPress={() => {
        this.setState({
          ...this.state,
          modalVisible: true,
          selectedDisc: kiekko
        })
      }}>
        <Text style={styles.text}>
          {discBasics(kiekko)}
        </Text>
      </TouchableOpacity>
    </View>
  )
}

discBasics = kiekko => {
  return `${kiekko.muovi} ${kiekko.mold} ${kiekko.paino}g (${kiekko.vari}) ${kiekko.kunto}/10`
}

discStats = kiekko => {
  return `${kiekko.nopeus} / ${kiekko.liito} / ${kiekko.vakaus} / ${kiekko.feidi}`
}

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
    fontSize: 15,
    padding: 5
  },
  icon: {
    width: 24,
    height: 24
  },
  discImage: {
    width: 300,
    height: 300
  }
});
