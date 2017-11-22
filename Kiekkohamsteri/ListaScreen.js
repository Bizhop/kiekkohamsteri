import React, { Component } from 'react'
import { View, FlatList, Alert, ActivityIndicator, StyleSheet } from 'react-native'
import { List, ListItem, Icon } from 'react-native-elements'
import R from 'ramda'
import { NavigationActions } from 'react-navigation'

import Api from './Api'
import Helpers from './Helpers'

export default class HomeScreen extends Component {
  static navigationOptions = props => {
    return {
      title: 'Kiekkolistaus',
      headerLeft: (
        <Icon name="home" size={35} onPress={() => props.navigation.dispatch(resetAction)} />
      ),
    }
  }

  constructor(props) {
    const { params } = props.navigation.state
    super(props)
    this.state = {
      user: params.user,
      kiekot: null,
    }
  }

  componentDidMount() {
    this.getDiscs()
  }

  render() {
    return this.state.kiekot ? (
      <View style={{ flex: 1 }}>
        <List>
          <FlatList
            data={this.state.kiekot}
            keyExtractor={(item, index) => item.id}
            renderItem={this.discRow}
          />
        </List>
      </View>
    ) : (
      <View style={styles.container}>
        <ActivityIndicator size="large" />
      </View>
    )
  }

  discRow = ({ item }) => {
    const { navigate } = this.props.navigation
    return (
      <ListItem
        roundAvatar
        title={Helpers.discBasics(item)}
        subtitle={Helpers.discStats(item)}
        avatar={{ uri: `${Helpers.imagesUrl}t_lista/${item.kuva}` }}
        onPress={() => {
          navigate('Kiekko', { selectedDisc: item, user: this.state.user })
        }}
      />
    )
  }

  getDiscs() {
    if (Api.login(this.state.user.idToken)) {
      Api.get(this.state.user.idToken)
        .then(response => {
          this.setState({
            ...this.state,
            kiekot: R.pathOr(null, ['content'], response),
          })
        })
        .catch(err => {
          Alert.alert('Kiekkojen haku epäonnistui', err.message)
        })
        .done()
    }
  }
}

const resetAction = NavigationActions.reset({
  index: 0,
  actions: [NavigationActions.navigate({ routeName: 'Home' })],
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
  },
})
