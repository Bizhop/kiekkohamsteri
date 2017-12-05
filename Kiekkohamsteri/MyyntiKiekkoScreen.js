import React, { Component } from 'react'
import { StyleSheet, Text, View, ScrollView, Image, ActivityIndicator } from 'react-native'
import R from 'ramda'

import Helpers from './Helpers'

export default class MyyntiKiekkoScreen extends Component {
  static navigationOptions = {
    title: 'Myytävänä'
  }

  constructor(props) {
    const { params } = props.navigation.state
    console.log(params)
    super(props)

    this.state = {
      user: params.user,
      selectedDisc: params.selectedDisc
    }
  }

  render() {
    const tussit = [
      {
        id: 1,
        text: 'Ei ole'
      },
      {
        id: 2,
        text: 'Rimmissä'
      },
      {
        id: 3,
        text: 'Pohjassa'
      },
      {
        id: 4,
        text: 'Rimmi + pohja'
      },
      {
        id: 5,
        text: 'Kannessa'
      },
      {
        id: 6,
        text: 'Kaikkialla'
      }
    ]
    const disc = this.state.selectedDisc
    return disc ? (
      <ScrollView style={styles.scrollContainer}>
        <Text>Myyjä: {disc.omistaja}</Text>
        <Text>{Helpers.discBasics(disc)}</Text>
        <Text>{Helpers.discStats(disc)}</Text>
        <Image
          source={{ uri: `${Helpers.imagesUrl}t_kiekko/${disc.kuva}` }}
          style={styles.discImage}
        />
        <Text>Tussit: {R.prop('text', R.find(R.propEq('id', disc.tussit))(tussit))}</Text>
        <Text>Paino: {disc.paino}</Text>
        {disc.hohto && <Text>Hohtava</Text>}
        {disc.spessu && <Text>Spesiaali</Text>}
        {disc.dyed && <Text>Värjätty</Text>}
        {disc.swirly && <Text>Swirly</Text>}
        {disc.loytokiekko && <Text>Löytökiekko</Text>}
        <Text>Muuta: {disc.muuta}</Text>
        <Text>Hinta: {disc.hinta}</Text>
      </ScrollView>
    ) : (
      <View style={styles.container}>
        <ActivityIndicator size="large" />
      </View>
    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#FFFFFF'
  },
  scrollContainer: {
    flex: 1,
    alignItems: 'flex-start',
    height: 1000,
    backgroundColor: '#FFFFFF'
  },
  discImage: {
    width: 300,
    height: 300
  }
})
