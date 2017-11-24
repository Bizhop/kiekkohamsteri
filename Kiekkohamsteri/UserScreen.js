import React, { Component } from 'react'
import { Alert, View, Text, ActivityIndicator } from 'react-native'

import Api from './Api'

export default class UserScreen extends Component {
  static navigationOptions = {
    title: 'Käyttäjä',
  }

  constructor(props) {
    const { params } = props.navigation.state
    super(props)
    this.state = {
      user: params.user,
      userId: params.userId,
      userDetails: null,
      updateDetails: {},
    }
  }

  componentDidMount() {
    this.initDetails()
  }

  initDetails = () => {
    Api.getUser(this.state.user.idToken, this.state.userId)
      .then(response => {
        this.setState({
          ...this.state,
          userDetails: response,
        })
      })
      .catch(err => {
        Alert.alert('Error fetching user details', err.message)
      })
      .done()
  }

  render() {
    const user = this.state.userDetails
    return this.state.userDetails ? (
      <View>
        <Text>{user.username}</Text>
        <Text>{user.email}</Text>
        <Text>{`${user.etunimi} ${user.sukunimi}`}</Text>
        <Text>{user.pdga_num}</Text>
      </View>
    ) : (
      <View>
        <ActivityIndicator size="large" />
      </View>
    )
  }
}
