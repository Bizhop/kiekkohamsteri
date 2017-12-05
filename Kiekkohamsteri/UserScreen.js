import React, { Component } from 'react'
import R from 'ramda'
import { Alert, View, Text, ActivityIndicator, StyleSheet, TextInput } from 'react-native'
import { Button } from 'react-native-elements'

import Api from './Api'

export default class UserScreen extends Component {
  static navigationOptions = {
    title: 'Käyttäjä'
  }

  constructor(props) {
    const { params } = props.navigation.state
    super(props)

    this.state = {
      user: params.user,
      userId: params.userId,
      userDetails: null,
      updateDetails: {}
    }
  }

  componentDidMount() {
    this.initDetails()
  }

  initDetails = () => {
    const list = ['username', 'etunimi', 'sukunimi', 'pdga_num']
    const predicate = (val, key) => R.contains(key, list)
    Api.getUser(this.state.user.idToken, this.state.userId)
      .then(response => {
        this.setState({
          ...this.state,
          userDetails: response,
          updateDetails: R.pickBy(predicate, response)
        })
      })
      .catch(err => {
        Alert.alert('Käyttäjän tietojen haku epäonnistui', err.message)
      })
      .done()
  }

  updateUserDetails = obj => {
    this.setState({
      ...this.state,
      updateDetails: R.mergeDeepRight(this.state.updateDetails, obj)
    })
  }

  render() {
    const user = this.state.userDetails
    const update = this.state.updateDetails
    return user ? (
      <View style={styles.container}>
        <View style={styles.inputTextContainer}>
          <Text style={styles.label}>Email</Text>
          <Text style={styles.input}>{user.email}</Text>
        </View>
        <View style={styles.inputTextContainer}>
          <Text style={styles.label}>Tunnus</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateUserDetails({ username: text })}
            value={update.username}
          />
        </View>
        <View style={styles.inputTextContainer}>
          <Text style={styles.label}>Etunimi</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateUserDetails({ etunimi: text })}
            value={update.etunimi}
          />
        </View>
        <View style={styles.inputTextContainer}>
          <Text style={styles.label}>Sukunimi</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateUserDetails({ sukunimi: text })}
            value={update.sukunimi}
          />
        </View>
        <View style={styles.inputTextContainer}>
          <Text style={styles.label}>PDGA#</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={text => this.updateUserDetails({ pdga_num: text })}
            value={update.pdga_num.toString()}
            keyboardType="numeric"
          />
        </View>
        <Button
          raised
          rightIcon={{ name: 'file-upload' }}
          backgroundColor="#0077ff"
          title="Päivitä tiedot"
          onPress={() => this.updateUser()}
        />
      </View>
    ) : (
      <View>
        <ActivityIndicator size="large" />
      </View>
    )
  }

  updateUser() {
    const { navigate } = this.props.navigation
    Api.updateUser({
      token: this.state.user.idToken,
      update: this.state.updateDetails,
      id: this.state.userId
    })
      .then(() => {
        navigate('Home')
      })
      .catch(err => {
        Alert.alert('Käyttäjän tietojen päivitys epäonnistui', err.message)
      })
      .done()
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#FFFFFF'
  },
  inputTextContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 5
  },
  textInput: {
    flex: 4,
    height: 40
  },
  label: {
    flex: 1
  },
  input: {
    flex: 4
  }
})
