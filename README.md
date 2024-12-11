# RUNBOOK

- javac *.java 
- Encode: cat <text to encode> | java HuffmanConverter encode <spec file> | java BitConverter encode <encoding type> > encoded_file.txt
- Decode: cat encoded_file.txt | java BitConverter decode binary | java HuffmanConverter decode <spec file> > decoded_file.txt

# TIME SPENT

- Estamated: ~10 hours

# NOTES 

- Debug statements were being mistakenly encoded, causing noise in my decoding. Stuck on this for a couple days.
- Had some trouble encoding the end or line charicter "\\e" since my encodeCharacter() function only encoded char, not string. Overloaded the method to fix this.

# RESOURCES AND ACKNOWLEDGEMENTS

- Emailed Proffessor Sklar for help with the incoding issue above, and he directed me to the solution. 
