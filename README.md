# hmvv
Houston Methodist Variant Viewer

Houston Methodist Variant Viewer (HMVV) is an application to support interpretation of Next Generation Sequencing (NGS) variants.

# Getting started

Download the latest .zip file from the list of releases:

https://github.com/hmvv/hmvv/releases

Unzip the file and edit config.ini with the parameters specific to your local environment (database server address and login credentials, linux server address and super user group)

Rezip the unzipped files after making the change to config.ini. Change the file extension from .zip to .jar.

# Set up your mysql tables

create a database named ngs

Download the latest mysqldump.zip.gz from https://github.com/hmvv/hmvv/releases

Decompress the archive with

tar -zxvf mysqldump.zip.gz

Load the data into your database:

mysql -u username -p ngs < schema.sql

mysql -u username -p ngs < cosmic_grch37v82_coordinates.sql

mysql -u username -p ngs < cosmic_grch37v82_identifiers.sql

# Set up your linux server
Setup your file structure to match that indicated by the constructCommandArray() method in hmvv/io/SampleEnterCommands.java.

The .txt files are produced by bash pipeline scripts (not yet uploaded to github). More information will be provided at a later date.

# Disclaimer

No guarantees of performance accompany this software, nor is any responsibility assumed on the part of the authors. Please read the licence
agreement.
