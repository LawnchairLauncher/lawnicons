import git
import html
import os
import sys
import requests

github_event_before = os.getenv('GITHUB_EVENT_BEFORE')
github_sha = os.getenv('GITHUB_SHA')
github_repository = os.getenv('GITHUB_REPOSITORY')
github_ref = os.getenv('GITHUB_REF')
telegram_ci_bot_token = os.getenv('TELEGRAM_CI_BOT_TOKEN')
telegram_ci_channel_id = os.getenv('TELEGRAM_CI_CHANNEL_ID')
artifact_directory = os.getenv('ARTIFACT_DIRECTORY')
discord_ci_bot_token = os.getenv('DISCORD_CI_BOT_TOKEN')

github_repository = "lawnchairlauncher/lawnicons"

def github_link():
    return f'https://github.com/{github_repository}/'

# Telegram
def send_message_to_ci_channel(message):
    data = {
        'chat_id': telegram_ci_channel_id,
        'parse_mode': 'HTML',
        'text': message,
        'disable_web_page_preview': 'true'
    }
    requests.post(
        url = f'https://api.telegram.org/bot{telegram_ci_bot_token}/sendMessage',
        data = data
    )

def send_document_to_ci_channel(document):
    data = {
        'chat_id': telegram_ci_channel_id,
    }
    files = {
        'document': document
    }
    requests.post(
        url = f'https://api.telegram.org/bot{telegram_ci_bot_token}/sendDocument',
        data = data,
        files = files
    )

def telegram_commit_message(commits, commits_range):
    overview_link = f'{github_link()}compare/{commits_range}'
    overview_link_tag = f'''<a href="{overview_link}">{len(commits)} new commit{'s' if len(commits) > 1 else ''}</a>'''
    message = f'''<b>🔨 {overview_link_tag} to <code>lawnicons:{github_ref}</code>:</b>\n'''

    for commit in reversed(commits):
        commit_message = commit.message.split('\n')[0]
        commit_link = f'{github_link()}commit/{commit.hexsha}'
        commit_link_tag = f'<a href="{commit_link}">{repository.git.rev_parse(commit.hexsha, short=7)}</a>'
        encoded_message = html.escape(commit_message)
        message += f'\n• {commit_link_tag}: {encoded_message}'
    return message

# Discord
def send_message_to_builds_channel(message):
    try:
        requests.post(
            discord_ci_bot_token,
            {
                "content": message
            }
        )
    except:
        print(sys.exception())

def send_document_to_builds_channel(document):
    try:
        files = {
            'payload_json': (None, '{"content": ""}'),
            'media': document
        }
        requests.post(discord_ci_bot_token, files=files)
    except:
        print(sys.exception())

def discord_commit_message(commits, commits_range):
    overview_link = f'{github_link()}compare/{commits_range}>'
    overview_link_tag = f'''[{len(commits)} new commit{'s' if len(commits) > 1 else ''}]({overview_link})'''
    message = f'''**🔨 {overview_link_tag} to `lawnicons:{github_ref}`:**\n'''

    for commit in reversed(commits):
        commit_message = commit.message.split('\n')[0]
        commit_link = f'{github_link()}commit/{commit.hexsha}>'
        commit_link_tag = f'[{repository.git.rev_parse(commit.hexsha, short=7)}]({commit_link})'
        encoded_message = html.escape(commit_message)
        message += f'\n* {commit_link_tag}: {encoded_message}'
    return message

repository = git.Repo('.')
commits_range = f'{github_event_before}...{github_sha}'

try:
	commits = list(repository.iter_commits(commits_range))
except git.exc.GitCommandError as error:
	print(f"Error fetching commits: {error}")
	exit(1)

telegram_message = telegram_commit_message(commits, commits_range)
discord_message = discord_commit_message(commits, commits_range)

if len(commits) != 0:
    send_message_to_ci_channel(message=telegram_message)
    send_message_to_builds_channel(message=discord_message)

    with open(f'{artifact_directory}/{os.listdir(artifact_directory)[0]}', 'rb') as apk:
        send_document_to_ci_channel(document=apk)

    with open(f'{artifact_directory}/{os.listdir(artifact_directory)[0]}', 'rb') as apk:
         send_document_to_builds_channel(document=apk)
