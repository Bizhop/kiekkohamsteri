import { Alert } from 'react-native'

const base = 'https://kiekkohamsteri-backend.herokuapp.com/api/'

const Api = {
    async get(endpoint, user) {
        const url = base + endpoint
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': user.idToken,
            }
        })
        const json = await response.json()
        return json
    },
    async ping() {
        const url = base + 'ping'
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'text/plain',
            }
        })
        return response
    },
    async login(user) {
        const url = base + 'auth/login'
        await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': user.idToken,
            }
        })
        .then((response) => {
            return response.email === user.email
        })
    },
    async upload(name, data){
        const url = base + 'upload'
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                data: `data:image/jpeg;base64,${data}`,
                name: name,
            })
        })
        const json = await response.json()
        return json
    }
}

export default Api
