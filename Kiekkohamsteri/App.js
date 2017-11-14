import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  TouchableOpacity,
  Alert,
  Image,
  Modal,
  FlatList,
  Picker,
  TextInput
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
      kiekkoUpdate: {},
      dropdowns: {},
    };
  }

  componentDidMount() {
    this._setupGoogleSignin();
    this.pingBackend();
  }

  updateState = obj => {
    this.setState(R.mergeDeepRight(this.state, obj))
  }

  updateKiekkoUpdate = obj => {
    this.setState({
      ...this.state,
      kiekkoUpdate: R.mergeDeepRight(this.state.kiekkoUpdate, obj)
    })
  }

  pingBackend() {
    setTimeout(() => this.pingBackend(), 30000)
      Api.ping()
      .then((response) => {
        this.updateState({ backendUp: true })
      })
      .catch((e) => {
        Alert.alert('Error', e.message)
      })
      .done()

  }

  render() {
    return (
      <View style={{flex: 1}}>
        <Modal
          animationType="slide"
          transparent={false}
          visible={this.state.kiekkoVisible}
          onRequestClose={() => {
            this.getDiscs()
            this.updateState({
              kiekkoVisible: false,
              listaVisible: true,
            })
          }}
          >
          {this.kiekkoModalContents()}
        </Modal>
        <Modal
          animationType="slide"
          transparent={false}
          visible={this.state.listaVisible}
          onRequestClose={() => {
            this.updateState({ listaVisible: false })
          }}
          >
          {this.kiekkoListContents()}
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

  kiekkoModalContents = () => {
    return this.state.selectedDisc ? (
      <ScrollView style={styles.scrollContainer}>
        <Text>{discBasics(this.state.selectedDisc)}</Text>
        <Text>{discStats(this.state.selectedDisc)}</Text>
        <Image 
          source={{uri: `${imagesUrl}t_kiekko/${this.state.selectedDisc.kuva}`}} 
          style={styles.discImage}
        />
        <View style={styles.inputRow}>
          <Text>Valmistaja</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.valmId}
            onValueChange={(itemValue, itemIndex) => {
              Api.dropdowns(itemValue)
              .then((dropdowns) => {
                this.updateState({ dropdowns: dropdowns })
                this.updateKiekkoUpdate({ valmId: itemValue })
              })
            }}
          >
            {this.state.dropdowns.valms.map(v => <Picker.Item key={v.id} label={v.valmistaja} value={v.id} />)}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text>Mold</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.moldId}
            onValueChange={(itemValue, itemIndex) => this.updateKiekkoUpdate({ moldId: itemValue })}
          >
          {this.state.dropdowns.molds.map(v => <Picker.Item key={v.id} label={v.kiekko} value={v.id} />)}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text>Muovi</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.muoviId}
            onValueChange={(itemValue, itemIndex) => this.updateKiekkoUpdate({ muoviId: itemValue })}
          >
          {this.state.dropdowns.muovit.map(v => <Picker.Item key={v.id} label={v.muovi} value={v.id} />)}
          </Picker>
        </View>
        <View style={styles.inputRow}>
          <Text>Väri</Text>
          <Picker
            style={styles.picker}
            selectedValue={this.state.kiekkoUpdate.variId}
            onValueChange={(itemValue, itemIndex) => this.updateKiekkoUpdate({ variId: itemValue })}
          >
          {this.state.dropdowns.varit.map(v => <Picker.Item key={v.id} label={v.vari} value={v.id} />)}
          </Picker>
        </View>
        <View style={styles.inputTextContainer}>
          <Text>Paino</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={(text) => this.updateKiekkoUpdate({ paino: text })}
            value={this.state.kiekkoUpdate.paino.toString()}
            keyboardType='numeric'
          />
        </View>
        <View style={styles.inputTextContainer}>
          <Text>Muuta</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={(text) => this.updateKiekkoUpdate({ muuta: text })}
            value={this.state.kiekkoUpdate.muuta}
          />
        </View>
        <Text>Kunto: {this.state.selectedDisc.kunto}</Text>
        <Text>Hohto: {this.state.selectedDisc.hohto && 'X'}</Text>
        <Text>Spesiaali: {this.state.selectedDisc.spessu && 'X'}</Text>
        <Text>Värjätty: {this.state.selectedDisc.dyed && 'X'}</Text>
        <Text>Swirly: {this.state.selectedDisc.swirly && 'X'}</Text>
        <Text>Tussit: {this.state.selectedDisc.tussit}</Text>
        <Text>Myynnissä: {this.state.selectedDisc.myynnissä && 'X'}</Text>
        <Text>Hinta: {this.state.selectedDisc.hinta}</Text>
        <Text>Löytökiekko: {this.state.selectedDisc.loytokiekko && 'X'}</Text>
        <Text>In the bag: {this.state.selectedDisc.itb && 'X'}</Text>
        <Button
          raised
          rightIcon={{name: 'file-upload'}}
          backgroundColor='#0077ff'
          title='Päivitä tiedot'
          onPress={() => this.updateDisc()}
        />
      </ScrollView>
    ) : (
      <View style={styles.container}>
        <Text>Ei valittua kiekkoa</Text>
      </View>
    )
  }

  kiekkoListContents = () => (
    <List>
      <FlatList
        data={this.state.kiekot}
        keyExtractor={(item, index) => item.id}
        renderItem={this.discRow}
      />
    </List>
  )

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
        this.updateState({
          kiekot: R.pathOr([], ['content'], response),
          listaVisible: true,
        })
      })
      .catch((err) => {
        Alert.alert('Kiekkojen haku epäonnistui', err.message)
      })
      .done()
    }
  }

  updateDisc() {
    if(Api.login(this.state.user.idToken)){
      Api.put('kiekot', {
        token: this.state.user.idToken,
        kiekko: this.state.kiekkoUpdate,
        id: this.state.selectedDisc.id
      })
      .then((response) => {
        this.updateState({ selectedDisc: response })
        Alert.alert('Kiekon päivitys onnistui!')
      })
      .catch((err) => {
        Alert.alert('Kiekon päivitys epäonnistui', err.message)
      })
      .done()
    }
  }

  discRow = ({item}) => (
    <ListItem
      roundAvatar
      title={discBasics(item)}
      subtitle={discStats(item)}
      avatar={{uri: `${imagesUrl}t_lista/${item.kuva}`}}
      onPress={() => {
        Api.dropdowns(item.valmId)
        .then((dropdowns) => {
          this.updateState({
            kiekkoVisible: true,
            listaVisible: false,
            selectedDisc: item,
            dropdowns: dropdowns,
            kiekkoUpdate: {
              moldId: item.moldId,
              muoviId: item.muoviId,
              variId: item.variId,
              valmId: item.valmId,
              paino: item.paino,
              muuta: item.muuta
            },
          })
        })
        .done()
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
        this.updateState({ user: user })
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
      this.updateState({ user: user })
    })
    .catch((err) => {
      Alert.alert('Google signin failed', `${err.code}: ${err.message}`);
    })
    .done();
  }

  _signOut() {
    GoogleSignin.revokeAccess().then(() => GoogleSignin.signOut()).then(() => {
      this.updateState({ user: null })
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
  return `${kiekko.valmistaja} ${kiekko.muovi} ${kiekko.mold} ${kiekko.paino}g (${kiekko.vari}) ${kiekko.kunto}/10`
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
  scrollContainer: {
    flex: 1,
    alignItems: 'flex-start',
    height: 1000
  },
  inputRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  inputTextContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 5
  },
  textInput: {
    height: 40,
    width: 300,
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
  },
  picker: {
    width: 150
  }
});
