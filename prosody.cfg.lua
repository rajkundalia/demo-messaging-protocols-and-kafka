-- Prosody XMPP Server Configuration
-- Save this as prosody.cfg.lua in your project directory

-- Server-wide settings
daemonize = false
pidfile = "/var/run/prosody/prosody.pid"

-- Logging configuration
log = {
    info = "/var/log/prosody/prosody.log";
    error = "/var/log/prosody/prosody.err";
    "*syslog";
}

-- This is the list of modules Prosody will load on startup.
modules_enabled = {
    -- Generally required
    "roster"; -- Allow users to have a roster. Recommended ;)
    "saslauth"; -- Authentication for clients and servers. Recommended if you want to log in.
    "tls"; -- Add support for secure TLS on c2s/s2s connections
    "dialback"; -- s2s dialback support
    "disco"; -- Service discovery
    "carbons"; -- Keep multiple clients in sync
    "pep"; -- Enables users to publish their avatar, mood, activity, playing music and more
    "private"; -- Private XML storage (for room bookmarks, etc.)
    "blocklist"; -- Allow users to block communications with other users
    "vcard4"; -- User profiles (stored in PEP)
    "vcard_legacy"; -- Conversion between legacy vCard and PEP Avatar, vcard-temp

    -- Nice to have
    "version"; -- Replies to server version requests
    "uptime"; -- Report how long server has been running
    "time"; -- Let others know the time here on this server
    "ping"; -- Replies to XMPP pings with pongs
    "register"; -- Allow users to register on this server using a client and change passwords
    "mam"; -- Store messages in an archive and allow users to access it
    "csi_simple"; -- Simple Mobile optimizations

    -- Admin interfaces
    "admin_adhoc"; -- Allows administration via an XMPP client that supports ad-hoc commands
    "admin_telnet"; -- Opens telnet console interface on localhost port 5582

    -- HTTP modules
    "bosh"; -- Enable BOSH clients, aka "Jabber over HTTP"
    "websocket"; -- XMPP over WebSockets
    "http_files"; -- Serve static files from a directory over HTTP

    -- Other specific functionality
    "limits"; -- Enable bandwidth limiting for XMPP connections
    "server_contact_info"; -- Publish contact information for this service
    "announce"; -- Send announcement to all online users
    "welcome"; -- Welcome users who register accounts
    "watchregistrations"; -- Alert admins of registrations
    "motd"; -- Send a message to users when they log in
    "legacyauth"; -- Legacy authentication. Only used by some old clients and bots.
}

-- These modules are auto-loaded, but should you want
-- to disable them then uncomment them here:
modules_disabled = {
    -- "offline"; -- Store offline messages
    -- "c2s"; -- Handle client connections
    -- "s2s"; -- Handle server-to-server connections
}

-- Disable account creation by default, for security
allow_registration = true

-- Force clients to use encrypted connections
c2s_require_encryption = false
s2s_require_encryption = false

-- Force certificate authentication for server-to-server connections
s2s_secure_auth = false

-- Some servers have invalid or self-signed certificates
s2s_insecure_domains = { "localhost" }

-- Even if you disable s2s_secure_auth, you can still require encryption
-- for some domains
s2s_secure_domains = { }

-- Select the authentication backend to use
authentication = "internal_hashed"

-- Select the storage backend to use
storage = "internal"

-- For the HTTP modules, set the path to serve files from
http_paths = {
    files = "/usr/share/prosody/http_files";
}

-- Logging configuration
log = {
    info = "/var/log/prosody/prosody.log";
    error = "/var/log/prosody/prosody.err";
    "*console";
}

-- Virtual hosts
VirtualHost "localhost"
    authentication = "internal_hashed"

    -- Assign this host a certificate for TLS
    ssl = {
        key = "/etc/prosody/certs/localhost.key";
        certificate = "/etc/prosody/certs/localhost.crt";
    }

-- Component "conference.localhost" "muc"
--     modules_enabled = {
--         "muc_mam";
--         "vcard_muc";
--     }

-- Set up an external component (default component port is 5347)
-- Component "gateway.localhost"
--     component_secret = "password"