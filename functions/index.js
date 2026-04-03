/**
 * Sends a data message when a new row is created under Realtime Database `Chats/{id}`.
 * Matches Android WhizzzFirebaseMessagingService payload keys (WhizzzStrings.Fcm).
 *
 * Deploy: npm install && firebase deploy --only functions
 * Requires Blaze + billing for Cloud Functions (Firebase pricing).
 */

const {onValueCreated} = require("firebase-functions/v2/database");
const {initializeApp} = require("firebase-admin/app");
const {getDatabase} = require("firebase-admin/database");
const {getMessaging} = require("firebase-admin/messaging");

initializeApp();

const NODE_USERS = "Users";
const NODE_TOKENS = "Tokens";
const CHILD_USERNAME = "username";
const CHILD_TOKEN = "token";

const FCM_USER = "user";
const FCM_ICON = "icon";
const FCM_BODY = "body";
const FCM_TITLE = "title";
const FCM_SENT = "sent";

exports.onNewChatMessage = onValueCreated(
    {
      ref: "/Chats/{messageId}",
      region: "us-central1",
    },
    async (event) => {
      const val = event.data.val();
      if (!val || typeof val !== "object") return;

      const receiverId = val.receiverId;
      const senderId = val.senderId;
      const messageText = typeof val.message === "string" ? val.message : "";

      if (!receiverId || !senderId) return;

      const db = getDatabase();

      const tokenSnap = await db.ref(`${NODE_TOKENS}/${receiverId}/${CHILD_TOKEN}`).get();
      const token = tokenSnap.val();
      if (!token || typeof token !== "string") return;

      let senderName = "User";
      const nameSnap = await db.ref(`${NODE_USERS}/${senderId}/${CHILD_USERNAME}`).get();
      if (nameSnap.exists && typeof nameSnap.val() === "string") {
        senderName = nameSnap.val();
      }

      const body = `${senderName}: ${messageText}`;

      try {
        await getMessaging().send({
          token,
          data: {
            [FCM_USER]: String(senderId),
            [FCM_ICON]: "ic_notification",
            [FCM_BODY]: body,
            [FCM_TITLE]: "New Message",
            [FCM_SENT]: String(receiverId),
          },
        });
      } catch (e) {
        console.error("FCM send failed", e);
      }
    },
);
