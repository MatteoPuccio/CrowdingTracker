import { getItemEncrypted } from "../utils/common";
import { accessToken } from "./login";

export async function doRemoveBookmark(bookmark, setBookmarks) {

    let accessToken = await getItemEncrypted("@access_token");

    fetch('http://10.0.2.2:8080/deleteBookmark', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: accessToken.token,
            bookmark: bookmark.id
        })
    })
        .then((response) => response.json())
        .then((fetchBookmarks(setBookmarks)))
}

export async function fetchBookmarks(setBookmarks) {

    let accessToken = await getItemEncrypted("@access_token");

    fetch('http://10.0.2.2:8080/getUserBookmarks', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: accessToken.token
        })
    })
        .then((response) => response.json())
        .then((json) => {
            console.log(json.bookmarks);
            setBookmarks(json.bookmarks);
        });
}

export async function addBookmark(bookmark, setBookmarks) {

    let accessToken = await getItemEncrypted("@access_token");

    fetch('http://10.0.2.2:8080/addBookmark', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: accessToken.token,
            bookmark: JSON.stringify(bookmark)
        })
    })
        .then(json => json.json())
        .then(json => {
            console.log("setting bookmarks");
            fetchBookmarks(setBookmarks)
        })
}

export async function getBookmarkBusyness(bookmark, setBusyness) {

    let accessToken = await getItemEncrypted("@access_token");

    fetch('http://10.0.2.2:8080/getBusynessFromBookmark', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: accessToken.token,
            bookmark: bookmark.id,
        })
    })
        .then((response) => response.json())
        .then((json) => {
            //console.log(JSON.stringify(json));
            if (json.hasOwnProperty('noData')) {
                setBusyness([]);
            } else {
                setBusyness(json.busyness);
            }
        })
}

export async function getBookmarkInfo(bookmark, setBookmarkInfo) {

    let accessToken = await getItemEncrypted("@access_token");

    fetch('http://10.0.2.2:8080/getInfoFromBookmark', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: accessToken.token,
            bookmark: bookmark.id,
        })
    })
        .then((response) => response.json())
        .then((json) => {
            if (json.status == 'error') {
                setBookmarkInfo(null);
            } else {
                setBookmarkInfo({ 'avgDwellTime': json.info.avgDwellTime });
            }
        })
}

export async function getReminder(bookmark, setReminder) {

    let accessToken = await getItemEncrypted("@access_token");

    fetch('http://10.0.2.2:8080/getBookmarkReminder', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: accessToken.token,
            bookmark: bookmark.id,
        })
    })
        .then(response => response.json())
        .then(json => {
            if (json.reminder != null) {
                //example format: 2022.08.29 12:43
                console.log(json.reminder.timestamp)
                let [fullYear, time] = (json.reminder.timestamp).split(' ');

                fullYear = fullYear.split('\.');
                time = time.split("\:")

                console.log(fullYear);
                console.log(time);

                let timestamp = new Date();

                timestamp.setFullYear(fullYear[0], fullYear[1] - 1, fullYear[2]);
                timestamp.setHours(time[0]);
                timestamp.setMinutes(time[1]);

                console.log(JSON.stringify({
                    'enabled': json.reminder.enabled,
                    'timestamp': timestamp
                }));

                setReminder({
                    'enabled': json.reminder.enabled,
                    'timestamp': timestamp
                });



            }
        })
}

export async function saveReminder(bookmark, reminder) {

    let accessToken = await getItemEncrypted("@access_token");

    fetch('http://10.0.2.2:8080/setBookmarkReminder', {
        method: 'POST',
        headers: {
            Accept: 'text/plain',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: accessToken.token,
            bookmark: bookmark.id,
            reminder: JSON.stringify(reminder),
        })
    })
}