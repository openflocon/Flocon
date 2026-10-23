#!/usr/bin/env bash
set -e

# Change to script directory
cd "$(dirname "$0")"

# Create a local virtual environment if it doesn't exist
if [ ! -d ".venv" ]; then
    echo "Creating local Python virtual environment (.venv)..."
    python3 -m venv .venv
    echo "Installing mkdocs-material..."
    .venv/bin/pip install mkdocs-material
fi

echo "Starting MkDocs live preview server..."
echo "Open http://127.0.0.1:8000 in your browser."
.venv/bin/mkdocs serve "$@"
