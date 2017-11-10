import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Alert,
  Image,
  Modal,
  FlatList
} from 'react-native';
import { List, ListItem, Button } from 'react-native-elements'
import R from 'ramda'
import {GoogleSignin, GoogleSigninButton} from 'react-native-google-signin';

import Api from './Api'

const imagesUrl = 'https://res.cloudinary.com/djc4j4dcs/'

export default class App extends Component {
  
  constructor(props) {
    super(props);
    this.state = {
      user: null,
      kiekot: [],
      backendUp: false,
      kiekkoVisible: false,
      listaVisible: false,
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
      .done()
    }
    catch (e) {
      Alert.alert('Error', e.message)
    }
  }

  render() {
    return (
      <View style={{flex: 1}}>
        <Modal
          animationType="slide"
          transparent={false}
          visible={this.state.kiekkoVisible}
          onRequestClose={() => {
            this.setState({
              ...this.state,
              kiekkoVisible: false,
              listaVisible: true,
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
        <Modal
          animationType="slide"
          transparent={false}
          visible={this.state.listaVisible}
          onRequestClose={() => {
            this.setState({
              ...this.state,
              listaVisible: false,
            })
          }}
          >
          <List>
            <FlatList
              data={this.state.kiekot}
              keyExtractor={(item, index) => item.id}
              renderItem={this.discRow}
            />
          </List>
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
        {this.state.user && this.state.backendUp && this.showButtons()}
      </View>
    );
  }

  showButtons = () => (
    <View style={styles.container}>
      <Button
        raised
        rightIcon={{name: 'file-download'}}
        backgroundColor='#0077ff'
        title='Hae kiekot'
        onPress={() => this.getDiscs()}
      />
    </View>
  )

  getDiscs() {
    if(Api.login(this.state.user.idToken)){
      Api.get('kiekot', this.state.user.idToken)
      .then((response) => {
        this.setState({
          ...this.state,
          kiekot: R.pathOr([], ['kiekot'], response),
          listaVisible: true,
        })
      })
      .catch((err) => {
        Alert.alert('Kiekkojen haku epäonnistui', err.message)
      })
      .done()
    }
  }

  discRow = ({item}) => (
    <ListItem
      roundAvatar
      title={discBasics(item)}
      subtitle={discStats(item)}
      avatar={{uri: `${imagesUrl}${item.kuva}`}}
      onPress={() => {
          this.setState({
            ...this.state,
            kiekkoVisible: true,
            listaVisible: false,
            selectedDisc: item,
          })
        }}
    />
  )

  async _setupGoogleSignin() {
    try {
      await GoogleSignin.hasPlayServices({ autoResolve: true });
      await GoogleSignin.configure({
        webClientId: '107543052765-lfgp4lke6h51a0l4kp258anilpeegf8v.apps.googleusercontent.com',
        offlineAccess: false
      });

      const user = await GoogleSignin.currentUserAsync();
      if(user) {
        console.log(user)
        this.setState({...this.state, user: user});
      }
    }
    catch(err) {
      Alert.alert('Play services error', `${err.code}: ${err.message}`);
    }
  }

  _signIn() {
    GoogleSignin.signIn()
    .then((user) => {
      console.log(user)
      this.setState({...this.state, user: user});
    })
    .catch((err) => {
      Alert.alert('Google signin failed', `${err.code}: ${err.message}`);
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
