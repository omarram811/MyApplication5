Source: https://www.twilio.com/docs/usage/tutorials/how-to-set-up-your-python-and-flask-development-environment


On cmd (msh Powershell): --------- (one-time setup)
python -m venv server_env
virtualenv server_env


----------------------------- (every new cmd terminal needs this)
server_env\Scripts\activate

--------------------- (inside env, install Flask packages, 1-time setup)
pip install Flask
pip install flask-sqlalchemy


---------------------- (running the server)
python app.py


----------------------- (running client using new cmd)
python client.py


----------------------- (creating database, 1-time setup, inside environment)
python
>>> from app import db
>>> db.create_all()

