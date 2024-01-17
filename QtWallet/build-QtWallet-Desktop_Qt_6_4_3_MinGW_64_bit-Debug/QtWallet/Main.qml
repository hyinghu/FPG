import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

ApplicationWindow {
    visible: true
    width: 640
    height: 480
    title: qsTr("Digital Asset App")

    ColumnLayout {
        anchors.fill: parent

        Item {
            Layout.fillWidth: true
            Layout.fillHeight: true

            ListView {
                width: parent.width
                height: parent.height - sendButton.height

                model: ListModel {
                    ListElement { assetName: "-----iiiiiiiiii Bitcoin" }
                    ListElement { assetName: "-----          Ethereum" }
                    ListElement { assetName: "-----iiiiiiiiiiii Litecoin" }
                }

                delegate: Item {
                    width: listView.width
                    height: 50

                    Rectangle {
                        width: parent.width
                        height: 50
                        color: "lightblue"

                        Text {
                            anchors.centerIn: parent
                            text: model.assetName
                        }
                    }
                }
            }
        }

        Button {
            text: "Send Digital Asset"
            Layout.alignment: Qt.AlignBottom
            onClicked: {
                console.log("Sending digital asset...");
            }
        }
    }
}
