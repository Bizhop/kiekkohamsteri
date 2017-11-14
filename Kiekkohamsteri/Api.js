import { Alert } from 'react-native'

const base = 'https://kiekkohamsteri-backend.herokuapp.com/api/'

const Api = {
    async get(token) {
        const url = `${base}kiekot?size=1000&sort=id,asc`
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token,
            }
        })
        const json = await response.json()
        return json
    },
    async put(params) {
        const url = `${base}kiekot/${params.id}`
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': params.token,
            },
            body: JSON.stringify(params.kiekko)
        })
        const json = await response.json()
        return json
    },
    async post(name, data, token){
        const url = base + 'kiekot'
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token,
            },
            body: JSON.stringify({
                data: `data:image/jpeg;base64,${data}`,
                name: name,
            })
        })
        const json = await response.json()
        return json
    },
    async delete(id, token){
        const url = `${base}kiekot/${id}`
        await fetch(url, {
            method: 'DELETE',
            headers: {
                'Authorization': token,
            },
        })
        .then(() => {return true})
        .catch((err) => {return false})
        .done()
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
    async login(token) {
        const url = base + 'auth/login'
        await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token,
            }
        })
        .then((response) => {
            return response.email ? true : false
        })
        .catch((error) => {
            return false
        })
        .done()
    },
    async dropdowns(valmId) {
        const url = base + 'dropdown?valmId='+valmId
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        const json = await response.json()
        return json
    }
}

export default Api
