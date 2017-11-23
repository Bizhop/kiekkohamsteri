import React, { Component } from 'react'
import { StyleSheet, Text, View, Alert, Image } from 'react-native'
import { Button } from 'react-native-elements'
import R from 'ramda'
import { GoogleSignin, GoogleSigninButton } from 'react-native-google-signin'

import Api from './Api'

export default class HomeScreen extends Component {
  static navigationOptions = {
    title: 'Kiekkohamsteri',
  }

  constructor(props) {
    super(props)
    this.state = {
      user: null,
      userId: null,
      backendUp: false,
    }
  }

  componentDidMount() {
    this._setupGoogleSignin()
    this.pingBackend()
  }

  updateState = obj => {
    this.setState(R.mergeDeepRight(this.state, obj))
  }

  pingBackend() {
    setTimeout(() => this.pingBackend(), 30000)
    Api.ping()
      .then(response => {
        this.updateState({ backendUp: true })
      })
      .catch(e => {
        Alert.alert('Error', e.message)
      })
      .done()
  }

  render() {
    return (
      <View style={{ flex: 1 }}>
        <View style={styles.headerContainer}>
          <Text style={styles.headerText}>Taustapalvelun tila </Text>
          {this.state.backendUp ? (
            <Image source={require('./images/green_light.png')} style={styles.icon} />
          ) : (
            <Image source={require('./images/red_light.png')} style={styles.icon} />
          )}
        </View>
        {this.state.user
          ? this.state.backendUp && this.showLogout()
          : this.state.backendUp && this.showLogin()}
        {this.state.backendUp && this.state.user && this.showButtons()}
      </View>
    )
  }

  showButtons = () => {
    const { navigate } = this.props.navigation
    return (
      <View style={styles.container}>
        <View style={styles.buttons}>
          <Button
            raised
            rightIcon={{ name: 'file-download' }}
            backgroundColor="#0077ff"
            title="Hae kiekot"
            onPress={() => navigate('Lista', { user: this.state.user })}
          />
        </View>
        <View style={styles.buttons}>
          <Button
            raised
            rightIcon={{ name: 'camera-alt' }}
            backgroundColor="#009933"
            title="Uusi kiekko"
            onPress={() => navigate('Camera', { user: this.state.user })}
          />
        </View>
      </View>
    )
  }

  async _setupGoogleSignin() {
    try {
      await GoogleSignin.hasPlayServices({ autoResolve: true })
      await GoogleSignin.configure({
        webClientId: '107543052765-lfgp4lke6h51a0l4kp258anilpeegf8v.apps.googleusercontent.com',
        offlineAccess: false,
      })

      const user = await GoogleSignin.currentUserAsync()
      if (user) {
        this.backendLogin(user)
      }
    } catch (err) {
      Alert.alert('Play services error', `${err.code}: ${err.message}`)
    }
  }

  _signIn() {
    GoogleSignin.signIn()
      .then(user => {
        this.backendLogin(user)
      })
      .catch(err => {
        Alert.alert('Google signin failed', `${err.code}: ${err.message}`)
      })
      .done()
  }

  _signOut() {
    GoogleSignin.revokeAccess()
      .then(() => GoogleSignin.signOut())
      .then(() => {
        this.updateState({ user: null })
      })
      .catch(err => {
        Alert.alert('Signout from google failed', err.message)
      })
      .done()
  }

  backendLogin = user => {
    console.log(user)
    Api.login(user.idToken)
      .then(response => {
        this.updateState({ user: user, userId: response.id })
      })
      .catch(err => {
        Alert.alert('Backend login failed', err.message)
      })
      .done()
  }

  showLogin = () => (
    <View style={styles.container}>
      <GoogleSigninButton
        style={{ width: 120, height: 44 }}
        color={GoogleSigninButton.Color.Light}
        size={GoogleSigninButton.Size.Icon}
        onPress={() => {
          this._signIn()
        }}
      />
    </View>
  )

  showLogout = () => {
    const { navigate } = this.props.navigation
    return (
      <View style={styles.container}>
        <Text style={styles.text}>
          {this.state.user.givenName} {this.state.user.familyName}: ({this.state.user.email})
        </Text>
        <Button
          raised
          rightIcon={{ name: 'settings' }}
          backgroundColor="#009933"
          title="Käyttäjän tiedot"
          onPress={() => navigate('User', { user: this.state.user, userId: this.state.userId })}
        />
        <Button
          raised
          rightIcon={{ name: 'error' }}
          backgroundColor="#ff3333"
          title="Kirjaudu ulos"
          onPress={() => this._signOut()}
        />
      </View>
    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
  },
  headerContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    backgroundColor: '#FFFFFF',
    justifyContent: 'center',
    alignItems: 'center',
    paddingTop: 50,
  },
  headerText: {
    color: 'black',
    fontSize: 24,
  },
  buttons: {
    padding: 10,
    width: 300,
  },
  text: {
    color: 'black',
    fontSize: 15,
    padding: 5,
  },
  icon: {
    width: 24,
    height: 24,
  },
})
