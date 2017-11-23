import React, { Component } from 'react'
import { View, FlatList, Alert, ActivityIndicator, StyleSheet, Modal } from 'react-native'
import { List, ListItem, Icon, Button, CheckBox } from 'react-native-elements'
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
      kiekotFiltered: [],
      predicates: [],
      showFilters: false,
    }
  }

  componentDidMount() {
    this.getDiscs()
  }

  render() {
    return this.state.kiekot ? (
      <View style={{ flex: 1 }}>
        <Modal
          visible={this.state.showFilters}
          onRequestClose={() => this.setState({ ...this.state, showFilters: false })}
        >
          <View>
            {this.myCheckBox('Hohto', 'hohto')}
            {this.myCheckBox('Spesiaali', 'spessu')}
            {this.myCheckBox('Värjätty', 'dyed')}
            {this.myCheckBox('Swirly', 'swirly')}
            {this.myCheckBox('Löytökiekko', 'loytokiekko')}
            {this.myCheckBox('In the bag', 'itb')}
            {this.myCheckBox('Myynnissä', 'myynnissa')}
            <Button
              raised
              rightIcon={{ name: 'filter-list' }}
              title="Suodata"
              backgroundColor="#009933"
              onPress={() => this.applyFilters()}
            />
          </View>
        </Modal>
        <View style={styles.filter}>
          <Button
            raised
            rightIcon={{ name: 'filter-list' }}
            title="Suodata"
            backgroundColor="#009933"
            onPress={() => this.setState({ ...this.state, showFilters: true })}
          />
        </View>
        <View style={styles.list}>
          <List>
            <FlatList
              data={this.state.kiekotFiltered}
              keyExtractor={(item, index) => item.id}
              renderItem={this.discRow}
            />
          </List>
        </View>
      </View>
    ) : (
      <View style={styles.container}>
        <ActivityIndicator size="large" />
      </View>
    )
  }

  myCheckBox(title, value) {
    return (
      <CheckBox
        title={title}
        checked={R.contains(value, R.pluck('name', this.state.predicates))}
        onPress={() =>
          this.appendOrRemovePredicate({ name: value, predicate: d => R.prop(value, d) })
        }
      />
    )
  }

  appendOrRemovePredicate = predicate => {
    const removePredicate = n => R.eqProps('name', n, predicate)
    R.contains(predicate.name, R.pluck('name', this.state.predicates))
      ? this.setState({
          ...this.state,
          predicates: R.reject(removePredicate, this.state.predicates),
        })
      : this.setState({ ...this.state, predicates: R.append(predicate, this.state.predicates) })
  }

  applyFilters = () => {
    this.setState({
      ...this.state,
      showFilters: false,
      kiekotFiltered: R.filter(
        R.allPass(R.pluck('predicate', this.state.predicates)),
        this.state.kiekot
      ),
    })
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
    Api.get(this.state.user.idToken)
      .then(response => {
        const kiekot = R.pathOr([], ['content'], response)
        this.setState({
          ...this.state,
          kiekot: kiekot,
          kiekotFiltered: kiekot,
        })
      })
      .catch(err => {
        Alert.alert('Kiekkojen haku epäonnistui', err.message)
      })
      .done()
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
  list: {
    flex: 9,
  },
  filter: {
    flex: 1,
    alignItems: 'flex-end',
  },
})
