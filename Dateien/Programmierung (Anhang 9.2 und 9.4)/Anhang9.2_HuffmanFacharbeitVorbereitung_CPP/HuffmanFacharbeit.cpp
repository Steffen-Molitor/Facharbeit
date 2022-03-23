#include <stdlib.h>
#include <iostream>
#include <string>
#include <istream>
#include <map>
#include <bitset>

using namespace std;


struct Node {
    char data;
    int weight;
    struct Node* left;
    struct Node* right;
    struct Node* parent;

    Node(char val, int wei) {
        data = val;
        weight = wei;
        left = NULL;
        right = NULL;
        parent = NULL;
    }
    Node() {
        data = NULL;
        weight = 0;
        left = NULL;
        right = NULL;
        parent = NULL;
    }
};

string reverse(string pText) {
    string result = "";
    for (int i = pText.length() - 1; i >= 0; i--) {
        result += pText[i];
    }
    return result;
}
string cutZeros(string pText) {
    string result = "";
    int i = 0;
    while (pText[i] == '0') {
        i++;
    }
    while (i < pText.length()) {
        result += pText[i];
        i++;
    }
    return result;
}
string cutZeros(string pText, bool keep) {
    string result = "";
    int i = 0;
    while (pText[i] == '0' && i < pText.length() - 1) {
        i++;
    }
    while (i < pText.length()) {
        result += pText[i];
        i++;
    }
    return result;
}
string cutTo(string pText, int pLength) {
    string result = "";
    int i = 0;
    while (i < pText.length() - pLength) {
        i++;
    }
    while (i < pText.length()) {
        result += pText[i];
        i++;
    }
    return result;
}
int binToDec(string bin) {
    int result = 0;
    for (int i = 0; i < bin.length(); i++) {
        if (bin[i] == '1') {
            result += pow(2, bin.length() - 1 - i);
        }
    }
    return result;
}

int main()
{
    cout << "Umsetzung der Huffman-Komprimierung als Vorbereitung auf die Facharbeit von Steffen Molitor (Angepasste Version fuer die Facharbeit)\n\n";
    string kd;
    string text;
    struct Node* root = new Node();
    do {
        cout << "Komprimieren, dekomprimieren, oder Fenster schliessen? k/d/e\n";
        getline(cin, kd);
        if (kd == "k") {
            cout << "Zu komprimierender Text: ";
            getline(cin, text);
            map<int, Node*> charMap;
            for (string::size_type i = 0; i < text.size(); i++) {
                int found = charMap.size();
                for (string::size_type j = 0; j < charMap.size(); j++) {
                    if (charMap[j]->data == (char)text.at(i)) {
                        found = j;
                    }
                }
                if (found == charMap.size()) {
                    charMap[found] = new Node(text.at(i), 1);
                }
                else {
                    charMap[found]->weight = charMap[found]->weight + 1;
                }
            }
            const int chars = charMap.size();
            bool flag = true;
            int zo = 1;
            struct Node* topNode = new Node();;
            int lowest;
            int parentless;
            if (chars > 2) {
                while (flag) {
                    flag = false;
                    lowest = -1;
                    parentless = 0;
                    for (string::size_type i = 0; i < charMap.size(); i++) {
                        if (charMap[i]->parent == NULL) {
                            if (lowest == -1 || charMap[i]->weight < charMap[lowest]->weight) {
                                lowest = i;
                            }
                            parentless++;
                        }
                    }
                    if (zo == 1) {
                        zo = 0;
                        topNode = new Node();
                    }
                    else {
                        zo = 1;
                    }
                    if (zo == 0) {
                        topNode->left = charMap[lowest];
                        topNode->weight += charMap[lowest]->weight;
                        charMap[lowest]->parent = topNode;
                        parentless--;
                    }
                    else {
                        topNode->right = charMap[lowest];
                        topNode->weight += charMap[lowest]->weight;
                        charMap[lowest]->parent = topNode;
                        charMap[charMap.size()] = topNode;
                    }
                    if (parentless > 2 || parentless == 2 && zo == 0) {
                        flag = true;
                    }
                }
            }
            zo = 0;
            for (string::size_type i = 0; i < charMap.size(); i++) {
                if (charMap[i] != NULL && charMap[i]->parent == NULL) {
                    if (zo == 0) {
                        root->left = charMap[i];
                        charMap[i]->parent = root;
                        zo = 1;
                    }
                    else {
                        root->right = charMap[i];
                        charMap[i]->parent = root;
                    }
                }
            }

            map<int, string[2]> codes;
            map<char, string> codetable;
            string code;
            int longest = 0;
            for (int i = 0; i < chars; i++) {
                codes[i][0] = charMap[i]->data;
                code = "";
                struct Node* temp = charMap[i];
                struct Node* parent;
                while (temp->parent != NULL || temp->parent == root) {
                    parent = temp->parent;
                    if (parent->left == temp) {
                        code += "0";
                    }
                    else {
                        code += "1";
                    }
                    temp = parent;
                }
                code = reverse(code);
                codes[i][1] = code;
                codetable[charMap[i]->data] = code;
                if (code.length() > longest) {
                    longest = code.length();
                }
            }
            string header;
            string binlongest = bitset<8>(longest).to_string();
            binlongest = cutZeros(binlongest);
            for (int i = 1; i < binlongest.length(); i++) {
                header += "0";
            }
            header += "1";

            for (string::size_type i = 0; i < codes.size(); i++) {
                header += cutTo(bitset<8>(codes[i][1].length()).to_string(), binlongest.length());
                header += codes[i][1];
                header += bitset<8>((int)codes[i][0][0]).to_string();
            }
            for (int i = 0; i < binlongest.length(); i++) {
                header += "0";
            }
            string finaltext = "";
            for (int i = 0; i < text.length(); i++) {
                finaltext += codetable[text.at(i)];
            }

            cout << "Komprimierter Text: " << header << finaltext << "\n\n" << endl;
        }
        else if (kd == "d") {
            cout << "Zu dekomprimierender Text: ";
            getline(cin, text);
            int lenlen = 0;
            int i = 0;
            while (text[i] == '0') {
                i++;
            }
            lenlen = i + 1;
            string zeroEnd = "";
            for (int j = 0; j < lenlen; j++) {
                zeroEnd += "0";
            }
            bool flag = true;
            string codelen = "";
            string code = "";
            map<string, char> chartable;
            while (flag) {
                code = "";
                codelen = "";
                for (int j = 0; j < lenlen; j++) {
                    i++;
                    codelen += text[i];
                }
                if (codelen != zeroEnd) {
                    for (int j = 0; j < binToDec(codelen); j++) {
                        i++;
                        code += text[i];
                    }
                    string charNum = "";
                    for (int j = 0; j < 8; j++) {
                        i++;
                        charNum += text[i];
                    }
                    chartable[code] = binToDec(charNum);
                }
                else {
                    flag = false;
                }
            }
            i++;
            string dektext = "";
            code = "";
            for (int j = i; j < text.length(); j++) {
                code += text[j];
                if (chartable[code] != NULL) {
                    dektext += chartable[code];
                    code = "";
                }
            }
            cout << "Dekomprimierter Text: " << dektext << "\n\n" << endl;
        }
        else if (kd != "e") {
            cout << "\n\nFalsche Eingabe\n\n";
            kd;
        }
    } while (kd != "e");
};
