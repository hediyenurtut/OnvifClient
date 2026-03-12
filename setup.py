from setuptools import setup, find_packages

with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

setup(
    name="onvif-client",
    version="0.1.0",
    author="hediyenurtut",
    description="A simple ONVIF client for IP cameras",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/hediyenurtut/OnvifClient",
    packages=find_packages(),
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires=">=3.7",
    install_requires=[
        "onvif-zeep>=0.2.12",
        "zeep>=4.2.1",
        "requests>=2.31.0",
    ],
)
