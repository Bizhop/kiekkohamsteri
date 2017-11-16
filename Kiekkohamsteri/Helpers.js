const imagesUrl = 'https://res.cloudinary.com/djc4j4dcs/'

export default class Helpers {
  static get imagesUrl() {
    return imagesUrl
  }

  static discBasics = kiekko => {
    return `${kiekko.valmistaja} ${kiekko.muovi} ${kiekko.mold} ${kiekko.paino}g (${kiekko.vari}) ${
      kiekko.kunto
    }/10`
  }

  static discStats = kiekko => {
    return `${kiekko.nopeus} / ${kiekko.liito} / ${kiekko.vakaus} / ${kiekko.feidi}`
  }
}
