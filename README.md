# HMVV

Houston Methodist Variant Viewer (HMVV) is an application to support interpretation of Next Generation Sequencing (NGS) variants.

# Getting started

Download the latest .zip file from the list of releases:

https://github.com/hmvv/hmvv/releases

Unzip the file and edit config.ini with the parameters specific to your local environment (database server address and login credentials, linux server address and super user group)

Rezip the unzipped files after making the change to config.ini. Change the file extension from .zip to .jar.

# Set up your mysql tables and databases

create a database named ngs_live

Download the latest mysqldump.zip.gz from https://github.com/hmvv/hmvv/releases

Decompress the archive with

tar -zxvf mysqldump.zip.gz

Load the data into your database:

mysql -u username -p ngs_live < schema.sql


The databases used by HMVV 3.3 application along with version number and release date:
- Catalogue Of Somatic Mutations In Cancer,cosmic-86,2018-08-14
- NCBI Clinical Variants,clinvar-4-2019,2019-04-01
- 1000 Genomes Project,g1000-phase3-v1,2015-08-18
- Oncology Knowledge Base,oncokb-19.1,2019-04-01
- Clinical Interpretations Of Variants In Cancer,civic-4-2019,2019-04-01
- The Genome Aggregation Database,gnomad-r-2.1.1,2019-03-06
- Variant Effect Predictor,vep-94,2018-10-01
- The Precision Medicine Knowledgebase,pmkb-4-2019,2019-04-10


# Set up your linux server
Setup your file structure to match that indicated by the constructCommandArray() method in hmvv/io/SampleEnterCommands.java.

The bioinformatics pipeline scripts associated with this application is uploaded to github - please see https://github.com/hmvv/ngs_pipelines.


# Disclaimer

No guarantees of performance accompany this software, nor is any responsibility assumed on the part of the authors. Please read the licence agreement.
